import { defineStore } from 'pinia'

export const useApperance = defineStore('navigate', {
  state: () => ({
    currentPathName: '',
  }),
  actions: {
    doWindowOpen(url: string, target?: string, features?: string) {
      window.open(url, target, features)
    },
    doRouteToByServiceName(serviceName: string) {
      console.log(serviceName)
    },
    doRouteToByPathName(path: string) {
      this.currentPathName = path
    },
  },
})
