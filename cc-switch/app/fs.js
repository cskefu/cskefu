const EventEmitter = require('events');
const _ = require('lodash');
const esl = require('modesl');
const debug = require('debug')('cc-switch:fs');
const config = require('./config');
const control = require('./control');
const { access } = require('fs');
const minio = require('./minio');
const moment = require('moment');

const { FREESWITCH_HOST, FREESWITCH_PORT, FREESWITCH_MAX_CHANNEL } = config;

const parseTable = body => {
  let list = [];
  let rows = body.split('\n');
  let first = rows.shift();
  if (first) {
    let head = first.split(',');
    for (let r of rows) {
      if (r) {
        let obj = {};
        let cell = r.split(',');
        for (let i = 0; i < head.length; i++) {
          obj[head[i]] = cell[i];
        }
        list.push(obj);
      } else break;
    }
  }

  return list;
};

const parseHeaders = headers => {
  let obj = {};
  for (let h of headers) {
    obj[h.name] = h.value;
  }
  return obj;
};

let conn;

const doConnect = reg =>
  new Promise((resolve, reject) => {
    conn = new esl.Connection(
      FREESWITCH_HOST,
      FREESWITCH_PORT,
      'ClueCon',
      () => {
        reg();
        resolve();
      },
    );

    conn.once('error', err => {
      debug('connect freeswitch error: %o', err);
      reject(err);

      setTimeout(() => doConnect(reg), 2000);
    });
  });

const show = cmd =>
  new Promise((resolve, reject) => {
    // debug('执行命令: %s', cmd);

    conn.bgapi(cmd, ({ body }) => {
      resolve(parseTable(body));
    });
  });

const showRegistrations = () => show('show registrations');

const showChannels = () => show('show channels');

const showCalls = () => show('show calls');

const record = uuid =>
  new Promise((resolve, reject) => {
    debug('开始录音: %s', uuid);

    conn.bgapi(
      `uuid_record ${uuid} start /usr/recordings/archive/${uuid}.wav`,
      ({ body }) => {
        console.log(body);
        resolve();
      },
    );
  });

const callOut = phone =>
  new Promise((resolve, reject) => {
    debug('拨打电话: %s', phone);

    conn.bgapi(`originate sofia/gateway/goipx/${phone} &park`, ({ body }) => {
      let match = body.match(/([a-f\d]{8}(-[a-f\d]{4}){3}-[a-f\d]{12}?)/i);
      if (match) {
        resolve(match[1]);
      } else {
        reject(new Error(`呼叫 ${phone} 失败`));
      }
    });
  });

const callSip = (phone, originate) =>
  new Promise((resolve, reject) => {
    debug('拨打SIP: %s', phone);

    ('originate user/1000 &park');

    conn.bgapi(
      `originate {origination_caller_id_number=${originate}}user/${phone} &park`,
      ({ body }) => {
        let match = body.match(/([a-f\d]{8}(-[a-f\d]{4}){3}-[a-f\d]{12}?)/i);
        if (match) {
          resolve(match[1]);
        } else {
          reject(new Error(`呼叫SIP ${phone} 失败`));
        }
      },
    );
  });

const bridge = (luuid, ruuid) =>
  new Promise((resolve, reject) => {
    debug('桥接: %s <--> %s', luuid, ruuid);

    conn.bgapi(`uuid_bridge ${luuid} ${ruuid}`, ({ body }) => {
      if (/OK/.test(body)) {
        resolve();
      } else {
        reject(new Error('桥接失败'));
      }
    });
  });

const eavesdrop = (sip, uuid) =>
  new Promise((resolve, reject) => {
    debug('监听: %s <--> %s', sip, uuid);

    conn.bgapi(`originate user/${sip} &eavesdrop(${uuid})`, ({ body }) => {
      if (/OK/.test(body)) {
        resolve();
      } else {
        reject(new Error('监听失败'));
      }
    });
  });

class FreeSwitch extends EventEmitter {
  constructor() {
    super();
    this.tasks = [];
    this.wait = [];
    this.sips = [];
  }

  async init() {
    debug('初始化Freeswich');
    await doConnect(() => this.regEvent());

    setInterval(async () => {
      let sips = await showRegistrations();
      let toDel = _.chain(await control.getSips())
        .map(s => s.no)
        .filter(k => !_.find(sips, { reg_user: k }))
        .value();
      if (toDel.length > 0) {
        await control.removeSips(toDel);
      }

      let channels = await showChannels();
      let updateSips = _.map(sips, ({ reg_user }) => {
        return {
          no: reg_user,
          state: _.find(channels, { dest: reg_user }) ? '通话' : '空闲',
        };
      });
      if (updateSips.length > 0) {
        await control.setSips(updateSips);
      }
    }, 2000);
  }

  regEvent() {
    conn.events('plain CUSTOM sofia::register');

    conn.on('esl::event::CUSTOM::*', ({ subclass, headers }) => {
      if (subclass == 'sofia::register') {
        let { username } = parseHeaders(headers);

        debug('SIP %s 已上线', username);
        control.setSips([{ no: username, state: '空闲' }]);
      }
    });

    conn.on('esl::event::CHANNEL_ANSWER::*', async ({ headers }) => {
      let variable = parseHeaders(headers);
      let { variable_uuid } = variable;
      debug('接通电话: %s', variable_uuid);

      let call = _.find(this.tasks, { uuid: variable_uuid });
      if (call) {
        for (let i = 0; i < 3; i++) {
          if (call.state == 'hangup') {
            return;
          }

          try {
            await call.linkSip();
            call.emit('channel_answer', variable_uuid);
            return;
          } catch (e) {
            debug('link sip error: %o', e);
          }
        }

        // debug('link sip error: %o', err);
      } else if (variable.variable_sip_gateway_name == 'goipx') {
        let {
          variable_bridge_channel,
          variable_sip_to_user: sip_to_user,
          variable_call_uuid,
        } = variable;
        let match = variable_bridge_channel.match(/sofia\/internal\/(\d+)@/);
        if (match) {
          let sip_from_user = match[1];
          let call = new CallOut(
            variable_uuid,
            sip_to_user,
            variable_call_uuid,
            sip_from_user,
          );
          this.tasks.push(call);
          call.emit('channel_answer', variable_uuid);
        }
      }
    });

    conn.on('esl::event::CHANNEL_HANGUP::*', ({ headers }) => {
      let event = parseHeaders(headers);

      let { variable_uuid } = event;
      debug('挂断电话: %s', variable_uuid);

      let call = _.find(this.tasks, { uuid: variable_uuid });
      if (call) {
        this.removeCall(call);
        call.state = 'hangup';
        call.emit('channel_hangup', variable_uuid);
      } else if (event.variable_sip_gateway_name == 'goipx') {
        let {
          variable_bridge_channel,
          variable_sip_to_user: sip_to_user,
          variable_call_uuid,
        } = event;
        let match =
          variable_bridge_channel &&
          variable_bridge_channel.match(/sofia\/internal\/(\d+)@/);
        if (match) {
          let sip_from_user = match[1];
          let call = new CallOut(
            variable_uuid,
            sip_to_user,
            null,
            sip_from_user,
          );

          call.emit('channel_hangup', variable_uuid);
        }
      }

      if (
        _.filter(this.tasks, { state: 'call' }).length <=
          FREESWITCH_MAX_CHANNEL &&
        this.wait.length > 0
      ) {
        debug('执行队列');
        let func = this.wait.shift();
        func();
      }
    });
  }

  removeCall(call) {
    _.remove(this.tasks, call);
  }

  call(phone) {
    let call = new Call(phone);
    this.tasks.push(call);

    let doCall = async () => {
      try {
        await call.start();
      } catch (err) {
        this.removeCall(call);
        call.emit('error', err);
      }
    };

    if (this.tasks.length <= FREESWITCH_MAX_CHANNEL) {
      doCall();
    } else {
      this.wait.push(doCall);
    }

    return call;
  }

  eavesdrop(sip, uuid) {
    return eavesdrop(sip, uuid);
  }
}

class Call extends EventEmitter {
  constructor(phone) {
    super();

    this.sips = [];
    this.phone = phone;
    this.try = 0;
  }

  async start() {
    this.uuid = await callOut(this.phone);
    this.state = 'call';
  }

  async linkSip() {
    debug('链接SIP电话');

    let sips = _.chain(await control.getSips())
      .filter({ state: '空闲' })
      .map(s => s.no)
      .value();

    this.sip = _.sample(_.intersection(sips, this.sips));
    if (!this.sip) {
      throw new Error('无可用SIP');
    }

    await control.setSips([{ no: this.sip, state: '通话' }]);

    this.sip_uuid = await callSip(this.sip, this.phone);
    await bridge(this.uuid, this.sip_uuid);
    await record(this.uuid);
    this.state = 'answer';
  }
}

class CallOut extends EventEmitter {
  constructor(uuid, phone, sip_uuid, sip) {
    super();

    this.uuid = uuid;
    this.phone = phone;
    this.sip_uuid = sip_uuid;
    this.sip = sip;

    let message = {
      uuid,
      from: sip,
      to: phone,
      channel: config.PBX_CHANNEL_ID,
      type: 'callout',
    };

    this.on('channel_answer', () => {
      debug('用户接听 %s -> %s', sip, phone);
      message.ops = 'answer';
      message.createtime = moment().valueOf();
      control.sendEvent(message);
    });

    this.on('channel_hangup', () => {
      debug('用户挂机 %s -> %s', sip, phone);
      message.ops = 'hangup';
      message.createtime = moment().valueOf();

      if (sip_uuid) {
        let file = `/usr/recordings/archive/${sip_uuid}.wav`;
        message.record = `${moment().format('YYYY-MM-DD')}/${uuid}.wav`;

        access(file, err => {
          if (err) {
            debug('录音文件不存在: %s', uuid);
          } else {
            debug('上传录音: %s', uuid);

            setTimeout(() => {
              minio.fPutObject('chatopera', message.record, file).catch(err => {
                debug('上传录音失败 %s error: %o', uuid, err);
              });
            }, 5000);
          }
        });
      }

      control.sendEvent(message);
    });
  }
}

module.exports = exports = new FreeSwitch();
