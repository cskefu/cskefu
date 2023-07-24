import { defineStore } from 'pinia'
import { Apperance } from '@cskefu/models'
import { useStorage } from '@vueuse/core'

export const useApperance = defineStore('apperance', {
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
    doToggleDarkMode() {
      this.apperance.darkMode = !this.apperance.darkMode
    },
    doSetLang(lang: string) {
      this.apperance.lang = lang
    },
  },
})
