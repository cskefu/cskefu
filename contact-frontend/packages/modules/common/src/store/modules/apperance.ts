import { defineStore } from 'pinia'
import { Apperance } from '@cskefu/models'
import { useStorage } from '@vueuse/core'

export const useApperance = defineStore('alerts', {
  state: () => ({
    apperance: useStorage<Apperance>('apperance', {
      lang: 'zh-CN',
      darkMode: false,
    }),
  }),
  getters: {
    isDarkMode: (state) => state.apperance.darkMode,
    getLang: (state) => state.apperance.lang,
  },
  actions: {
    toggleDarkMode() {
      this.apperance.darkMode = !this.apperance.darkMode
    },
    setLang(lang: string) {
      this.apperance.lang = lang
    },
  },
})
