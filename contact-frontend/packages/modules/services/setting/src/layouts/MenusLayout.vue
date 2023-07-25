<script setup lang="ts">
import { MenusLayout } from '@cskefu/shared-ui'
import { MenuOption } from 'naive-ui'
import { Menu } from '@cskefu/shared-ui'
import { ref } from 'vue'

const menuOptions: MenuOption[] = []
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
