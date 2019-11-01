const Redis = require('ioredis');
const _ = require('lodash');
const config = require('./config');
const debug = require('debug')('cc-switch:control');

const FS_SIP_STATUS = `pbx:${config.PBX_CHANNEL_ID}:sips`;
const FS_CHANNE_CC_TO_FS = `pbx:${config.PBX_CHANNEL_ID}:execute`;
const FS_DIALPLAN_STATUS = `pbx:${config.PBX_CHANNEL_ID}:status`;
const FS_DIALPLAN_TARGET = `pbx:${config.PBX_CHANNEL_ID}:targets`;
const FS_EVENT_TO_CC = `pbx:${config.PBX_CHANNEL_ID}:events`;

const sub = new Redis({
  host: config.REDIS_HOST,
  port: config.REDIS_PORT,
  db: 2,
});

const redis = new Redis({
  host: config.REDIS_HOST,
  port: config.REDIS_PORT,
  db: 2,
});

module.exports = exports = {
  subExecute(fn) {
    return new Promise((resolve, reject) => {
      debug('订阅控制信道');
      sub.subscribe(FS_CHANNE_CC_TO_FS, (err, count) => {
        if (err) {
          debug('订阅失败: %o', err);
          reject(err);
        } else {
          debug('订阅成功: %s', count);
          sub.on('message', (channel, message) => {
            if (channel == FS_CHANNE_CC_TO_FS) {
              message = JSON.parse(message);
              fn(message);
            }
          });
          resolve();
        }
      });
    });
  },
  getStatus() {
    return redis.hgetall(FS_DIALPLAN_STATUS).then(rows =>
      _.map(rows, (v, k) => {
        let obj = JSON.parse(v);
        obj.id = k;
        return obj;
      }),
    );
  },
  getNextCall(dialplanId) {
    return redis
      .rpop(`${FS_DIALPLAN_TARGET}:${dialplanId}`)
      .then(data => JSON.parse(data));
  },
  getSips() {
    return redis.hgetall(FS_SIP_STATUS).then(sips =>
      _.map(sips, (v, k) => ({
        no: k,
        state: v,
      })),
    );
  },
  removeSips(nos) {
    return redis.hdel(FS_SIP_STATUS, ...nos);
  },
  setSips(sips) {
    let args = [];
    _.forEach(sips, s => {
      args.push(s.no);
      args.push(s.state);
    });
    return redis.hmset(FS_SIP_STATUS, ...args);
  },
  sendEvent(data) {
    return redis.publish(FS_EVENT_TO_CC, JSON.stringify(data));
  },
};
