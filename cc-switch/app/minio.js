const Minio = require('minio');
const config = require('./config');

let client = new Minio.Client({
  endPoint: config.MINIO_END_POINT,
  accessKey: config.MINIO_ACCESS_KEY,
  secretKey: config.MINIO_SECRET_KEY,
  port: 9000,
  useSSL: false,
});

module.exports = exports = client;
