import { createApp } from 'vue'
import App from './App.vue'
import './index.css'
import router from './router'
import i18n from '@cskefu/i18n'

createApp(App).use(router).use(i18n).mount('#app')
