<script setup lang="ts">
import { MenusLayout } from '@cskefu/shared-ui'
import { MenuOption } from 'naive-ui'

import routes from '../routes'
import { routesToMenus } from '@cskefu/shared-utils'
import { Menu } from '@cskefu/shared-ui'

const menuOptions: MenuOption[] = routesToMenus(routes)
</script>
<template>
  <MenusLayout>
    <Menu :options="menuOptions" />
  </MenusLayout>
</template>
