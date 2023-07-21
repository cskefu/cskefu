import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    // 是可选的
    icon?: string,
    comingSoon?: boolean

    // 每个路由都必须声明
    requiresAuth: boolean
    title: string
  }
}
