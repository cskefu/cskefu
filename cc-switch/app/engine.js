const _ = require('lodash');
const debug = require('debug')('cc-switch:engine');

const config = require('./config');
const control = require('./control');
const Dialplan = require('./dialplan');
const fs = require('./fs');

class Engine {
  constructor() {
    this.dialplans = [];
  }

  createDialplan(id, concurrency, sips) {
    debug('创建呼叫计划: %s ,并发: %s', id, concurrency);
    if (!_.find(this.dialplans, { id })) {
      let dialplan = new Dialplan(id, concurrency, sips);
      this.dialplans.push(dialplan);
      dialplan.start(() => _.remove(this.dialplans, dialplan));
    }
  }

  async init() {
    await fs.init();

    await control.subExecute(message => {
      debug('接收控制命令: %o', message);
      let { ops, channel } = message;
      if (channel == config.PBX_CHANNEL_ID) {
        if (ops == 'start') {
          this.createDialplan(
            message.dialplan,
            message.concurrency,
            message.sips,
          );
        } else if (ops == 'monitor') {
          fs.eavesdrop(message.sip, message.uuid);
        } else {
          let dialplan = _.find(this.dialplans, { id: message.dialplan });
          if (dialplan) {
            dialplan.stop();
            _.remove(this.dialplans, dialplan);
          }
        }
      }
    });

    debug('获取初始状态');
    let dialplanTasks = await control.getStatus();

    for (let task of dialplanTasks) {
      let { id, concurrency, status } = task;
      if (status == '执行中') {
        this.createDialplan(id, concurrency);
      }
    }
  }
}

module.exports = exports = Engine;
