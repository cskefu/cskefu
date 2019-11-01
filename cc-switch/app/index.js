const debug = require('debug')('cc-switch');
const Engine = require('./engine');

const engine = new Engine();

engine
  .init()
  .then(() => {
    console.log('cc-switch started');
  })
  .catch(err => {
    debug('cc-switch start error: %o', err);
    process.exit();
  });
