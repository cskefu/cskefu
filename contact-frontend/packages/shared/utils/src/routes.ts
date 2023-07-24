import { MenuOption, NIcon } from 'naive-ui'
import { Component, h } from 'vue'
import { RouteRecordRaw } from 'vue-router'

function renderIcon(icon?: Component) {
  if (icon) {
    return () => h(NIcon, null, { default: () => h(icon) })
  }
}

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
          icon: renderIcon(route.meta?.icon),
          children: routesToMenus(route.children, true),
        })
      } else {
        result.push({
          label: route.meta?.title,
          key: route.name as string,
          icon: renderIcon(route.meta?.icon),
          disabled: route.meta?.disabled as boolean,
          comingSoon: route.meta?.comingSoon,
        })
      }
    })
  }
  return result
}
