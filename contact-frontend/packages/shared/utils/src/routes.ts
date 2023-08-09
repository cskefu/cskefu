import { MenuOption, NIcon } from 'naive-ui'
import { Component, h } from 'vue'
import { RouteRecordRaw, RouterLink } from 'vue-router'

function renderIcon(icon?: Component) {
  if (icon) {
    return () => h(NIcon, null, { default: () => h(icon) })
  }
}

export function routesToMenus(
  routes: RouteRecordRaw[],
  isNotRoot?: boolean,
  deep: boolean = true
): MenuOption[] {
  const result: MenuOption[] = []
  const array = isNotRoot ? routes : routes[0].children
  if (array) {
    array.forEach((route: RouteRecordRaw) => {
      if (route.children && deep) {
        result.push({
          label: h(
            RouterLink,
            {
              to: {
                name: route.name,
              },
            },
            { default: () => route.meta?.title as string }
          ),
          title: route.meta?.title,
          key: route.path,
          icon: renderIcon(route.meta?.icon),
          children: routesToMenus(route.children, true),
        })
      } else {
        result.push({
          label: h(
            RouterLink,
            {
              to: {
                name: route.name,
              },
            },
            { default: () => route.meta?.title as string }
          ),
          title: route.meta?.title,
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
