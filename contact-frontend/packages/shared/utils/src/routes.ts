import { MenuOption } from 'naive-ui'
import { RouteRecordRaw } from 'vue-router'

export function routesToMenus(
  routes: RouteRecordRaw[],
  isNotRoot?: boolean
): MenuOption[] {
  const result: MenuOption[] = []
  const array = isNotRoot ? routes : routes[0].children
  if (array) {
    array.forEach((route: RouteRecordRaw) => {
      if (route.children) {
        result.push({
          label: route.meta?.title,
          key: route.path,
          children: routesToMenus(route.children, true),
        })
      } else {
        result.push({
          label: route.meta?.title,
          key: route.name as string,
          disabled: route.meta?.disabled as boolean,
          comingSoon: route.meta?.comingSoon,
        })
      }
    })
  }
  return result
}
