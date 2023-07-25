<script setup lang="ts">
import { MenusLayout } from '@cskefu/shared-ui'
import { MenuOption, NBreadcrumbItem } from 'naive-ui'

import { Menu } from '@cskefu/shared-ui'
import { ROUTE_NAME } from '@cskefu/models'
import { ref, h } from 'vue'

const menuOptions: MenuOption[] = []
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
