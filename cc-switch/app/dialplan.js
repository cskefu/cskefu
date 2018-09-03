const _ = require('lodash');
const moment = require('moment');
const fs = require('./fs');
const control = require('./control');
const debug = require('debug')('cc-switch:dialplan');
const { access } = require('fs');
const minio = require('./minio');

class Dialplan {
  constructor(id, concurrency, sips) {
    this.id = id;
    this.concurrency = concurrency;
    this.tasks = [];
    this.sips = sips;
    this.state = 'create';
  }

  start(cb) {
    debug('开始呼叫计划: %o', this.id);
    this.time = setInterval(() => this.loop(), 1000);
    this.start = 'start';
    this.cb = cb;
  }

  callTask(to, channel) {
    debug('呼叫目标号码: %s', to);

    let message = { to, channel, dialplan: this.id, type: 'callout' };
    let call = fs.call(to);
    call.sips = this.sips;
    let removeThisCall = () => _.remove(this.tasks, call);

    call.on('channel_answer', uuid => {
      debug('用户接听 %s', to);
      message.uuid = uuid;
      message.from = call.sip;
      message.ops = 'answer';
      message.createtime = moment().valueOf();
      control.sendEvent(message);
    });

    call.on('channel_hangup', () => {
      debug('用户挂机 %s', to);
      message.ops = 'hangup';
      message.createtime = moment().valueOf();
      removeThisCall();

      if (call.state != 'call') {
        let file = `/usr/recordings/archive/${call.uuid}.wav`;
        message.record = `${moment().format('YYYY-MM-DD')}/${call.uuid}.wav`;

        access(file, err => {
          if (err) {
            debug('录音文件不存在: %s', call.uuid);
          } else {
            debug('上传录音: %s', call.uuid);

            setTimeout(() => {
              minio.fPutObject('chatopera', message.record, file).catch(err => {
                debug('上传录音失败 %s error: %o', call.uuid, err);
              });
            }, 5000);
          }
        });
      }

      control.sendEvent(message);
    });

    call.on('error', () => {
      debug('呼叫错误 %s', to);
      message.ops = 'hangup';
      removeThisCall();
      control.sendEvent(message);
    });

    return call;
  }

  async loop() {
    // debug('呼叫计划循环: %s', this.id);
    while (this.tasks.length < this.concurrency) {
      let info = await control.getNextCall(this.id);

      if (info) {
        let { to, channel } = info;
        let call = this.callTask(to, channel);
        this.tasks.push(call);
      } else {
        debug('完成呼叫计划: %s', this.id);
        this.start = 'finish';
        clearInterval(this.time);
        if (this.cb) {
          this.cb();
        }

        return;
      }
    }
  }

  async stop() {
    debug('停止呼叫计划: %s', this.id);
    clearInterval(this.time);
    // await Promise.all(this.tasks);
  }
}

module.exports = exports = Dialplan;
