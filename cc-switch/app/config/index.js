const fs = require('fs');
const path = require('path');
const dotenv = require('dotenv');

const PBX_CHANNEL_ID = 'test';

const FREESWITCH_HOST = 'localhost';
const FREESWITCH_PORT = 8021;
const FREESWITCH_MAX_CHANNEL = 5;

const MINIO_END_POINT = 'localhost';
const MINIO_ACCESS_KEY = 'key';
const MINIO_SECRET_KEY = 'secret';

const REDIS_HOST = 'localhost';
const REDIS_PORT = 6379;

const config = {
  PBX_CHANNEL_ID,
  FREESWITCH_HOST,
  FREESWITCH_PORT,
  FREESWITCH_MAX_CHANNEL,
  REDIS_HOST,
  REDIS_PORT,
  MINIO_END_POINT,
  MINIO_ACCESS_KEY,
  MINIO_SECRET_KEY,
};

let envFile = path.join(__dirname, 'dev.env');
if (fs.existsSync(envFile)) {
  dotenv.config({ path: envFile });
}

for (let key in config) {
  let value = process.env[key];
  if (value) {
    config[key] = value;
  }
}

module.exports = exports = config;
