const _ = require('lodash');

const PBX_CHANNEL_ID = 'test';

const FREESWITCH_HOST = 'localhost';
const FREESWITCH_PORT = 8021;
const FREESWITCH_MAX_CHANNEL = 5;

const ACTIVEMQ_HOST = 'localhost';
const ACTIVEMQ_PORT = 61613;
const ACTIVEMQ_USER = null;
const ACTIVEMQ_PASS = null;

const MINIO_END_POINT = '';
const MINIO_ACCESS_KEY = '';
const MINIO_SECRET_KEY = '';

const REDIS_HOST = 'localhost';
const REDIS_PORT = 6379;

const config = {
  PBX_CHANNEL_ID,
  FREESWITCH_HOST,
  FREESWITCH_PORT,
  FREESWITCH_MAX_CHANNEL,
  ACTIVEMQ_HOST,
  ACTIVEMQ_PORT,
  ACTIVEMQ_USER,
  ACTIVEMQ_PASS,
  REDIS_HOST,
  REDIS_PORT,
  MINIO_END_POINT,
  MINIO_ACCESS_KEY,
  MINIO_SECRET_KEY,
};

let env = {};

try {
  env = require('./dev.env.js');
} catch (e) {}

_.merge(env, process.env);

for (let key in config) {
  let value = env[key];
  if (value) {
    config[key] = value;
  }
}

module.exports = exports = config;
