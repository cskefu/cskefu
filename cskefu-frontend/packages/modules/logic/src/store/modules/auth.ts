import { defineStore } from 'pinia'

export const useApperance = defineStore('auth', {
  state: () => ({
    currentUser: {},
  }),
  actions: {
    doLogin() {},
    doLogout() {},
    doSocialLogin() {},
  },
})
