import { resolve } from 'path'
import { defineConfig } from 'vite'

import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import VueI18nPlugin from '@intlify/unplugin-vue-i18n/vite'

// https://vitejs.dev/config/
export default defineConfig({
  publicDir: resolve(__dirname, '../../../public'),
  plugins: [
    vue(),
    vueJsx(),
    VueI18nPlugin({
      include: [
        resolve(__dirname, 'node_modules/@cskefu/locales/src/locales/**'),
      ],
    }),
  ],
})
