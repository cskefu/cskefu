<script setup lang="ts">
import { MenusLayout } from '@cskefu/shared-ui'
import { MenuOption } from 'naive-ui'
import { Menu } from '@cskefu/shared-ui'

const menuOptions: MenuOption[] = []
</script>
<template>
  <MenusLayout>
    <Menu :options="menuOptions" />
  </MenusLayout>
</template>
