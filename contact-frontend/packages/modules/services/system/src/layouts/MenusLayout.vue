<script setup lang="ts">
import { MenusLayout } from '@cskefu/shared-ui'
import { MenuOption, NMenu } from 'naive-ui'

const menuOptions: MenuOption[] = []
</script>
<template>
  <MenusLayout>
    <n-menu :options="menuOptions" />
  </MenusLayout>
</template>
