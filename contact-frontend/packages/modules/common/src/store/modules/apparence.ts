import { defineStore } from 'pinia'
import { Apparence } from '@cskefu/models'

export const useApparence = defineStore('alerts', {
  state: (): Apparence => ({ lang: 'zh-CN', darkMode: false }),
  getters: {
    isDarkMode: (state) => state.darkMode,
    getLang: (state) => state.lang,
  },
  actions: {
    toggleDarkMode() {
      this.darkMode = !this.darkMode
    },
    changeLang(lang: string) {
      this.lang = lang
    },
  },
})
