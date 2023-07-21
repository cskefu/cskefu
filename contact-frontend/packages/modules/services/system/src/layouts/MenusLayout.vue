<script setup lang="ts">
import { MenusLayout } from '@cskefu/shared-ui'
import { MenuOption, NMenu } from 'naive-ui'

import routes from '../routes'

import { routesToMenus } from '@cskefu/shared-utils'

const menuOptions: MenuOption[] = routesToMenus(routes)
</script>
<template>
  <MenusLayout>
    <n-menu :options="menuOptions" />
  </MenusLayout>
</template>
