<script setup lang="ts">
import { ref, h } from 'vue'
import { MenuOption, NBreadcrumbItem } from 'naive-ui'

import { Menu } from '@cskefu/shared-ui'
import { MenusLayout } from '@cskefu/shared-ui'
import { routesToMenus } from '@cskefu/shared-utils'

import routes from '../routes'
import { ROUTE_NAME } from '@cskefu/models'

const menuOptions: MenuOption[] = routesToMenus(routes)
const collapsed = ref<boolean>(false)
</script>
<template>
  <MenusLayout
    v-model:collapsed="collapsed"
    :page-title="$route.meta.title"
    :page-icon="$route.meta.icon"
    :page-breadcrumbs="[
      h(NBreadcrumbItem, { to: { name: ROUTE_NAME.DASHBOARD_INDEX } }, '首页'),
      h(
        NBreadcrumbItem,
        { to: { name: $route.name } },
        $route.meta.title as string
      ),
    ]"
  >
    <Menu
      v-model:collapsed="collapsed"
      :options="menuOptions"
      :collapsed-width="64"
      :collapsed-icon-size="24"
    />
  </MenusLayout>
</template>
