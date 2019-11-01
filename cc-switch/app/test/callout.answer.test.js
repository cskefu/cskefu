/**
 * Redis Test
 */
const test = require('ava');
const debug = require('debug')('cc-switch:test:redis');
const Redis = require('ioredis');
const config = require('../config');
const util = require('util');
const moment = require('moment');


const redis = new Redis({
    host: config.REDIS_HOST,
    port: config.REDIS_PORT,
    db: 2,
});

const EVENT_TYPE_CALLOUT = "callout";
const EVENT_QUEUE_QUEUE_CC_TO_FS = "cc:to:freeswitch";
const CALLOUT_DIALPLAN_STATUS = "callout:dialplan:status";
const CALLOUT_DIALPLAN_TARGET = "freeswitch:%s:callout";
const CALLOUT_CC_FROM_FS = "pbx:bxzq:events";



test.only("Redis Test # 外呼接通", async(t) => {
    let now = moment();
    now.add(-3, "minutes");
    console.log("接通时间：", now);

    let payload = {
        "uuid": "9a0cbc81-ccae-425e-8d3d-369b872a6481",
        "to": "13213213213",
        "from": "1003",
        "type": "callout",
        "channel": "bxzq",
        "dialplan": "4028827365b2acec0165b307afe405de",
        "createtime": now.valueOf(),
        "ops": "answer"
    }

    redis.publish(CALLOUT_CC_FROM_FS, JSON.stringify(payload));
    t.pass();
})



