<script setup lang="ts">
import { ref } from 'vue'
import { MenuOption } from 'naive-ui'

import { Menu } from '@cskefu/shared-ui'
import { MenusLayout } from '@cskefu/shared-ui'
import { routesToMenus } from '@cskefu/shared-utils'

import routes from '../routes'

const menuOptions: MenuOption[] = routesToMenus(routes)
const collapsed = ref<boolean>(false)
</script>
<template>
  <MenusLayout v-model:collapsed="collapsed">
    <Menu
      v-model:collapsed="collapsed"
      :options="menuOptions"
      :collapsed-width="64"
      :collapsed-icon-size="24"
    />
  </MenusLayout>
</template>
