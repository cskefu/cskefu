import { defineStore } from 'pinia'
import { Apperance } from '@cskefu/models'

export const useApperance = defineStore('alerts', {
  state: (): Apperance => ({ lang: 'zh-CN', darkMode: false }),
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
