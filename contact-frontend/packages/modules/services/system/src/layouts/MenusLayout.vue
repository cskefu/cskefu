<script setup lang="ts">
import { MenusLayout } from '@cskefu/shared-ui'
import { MenuOption, NMenu } from 'naive-ui'

import routes from '../routes'

const menuOptions: MenuOption[] = routes[0]?.children?.map((route) => {
  return {
    label: route.meta?.title,
    key: route.name,
    disabled: route.meta?.disabled,
  }
}) as MenuOption[]
</script>
<template>
  <MenusLayout>
    <n-menu :options="menuOptions" />
  </MenusLayout>
</template>
