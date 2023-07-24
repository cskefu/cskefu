import { defineStore } from 'pinia'

export const useApperance = defineStore('auth', {
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
