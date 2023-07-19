import { App } from 'vue'
import { createI18n } from 'vue-i18n'

const messages = {
  'en-US': {
    message: {
      hello: 'hello world',
    },
  },
  'zh-CN': {
    message: {
      hello: '你好',
    },
  },
}

const i18n = createI18n({
  legacy: false,
  locale: 'zh-CN',
  messages,
  globalInjection: true,
  fallbackLocale: 'zh-CN',
})

const install = (app: App) => {
  app.use(i18n)
}

export default install
