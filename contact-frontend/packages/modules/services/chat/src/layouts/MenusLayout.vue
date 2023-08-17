<script setup lang="ts">
import { MenusLayout } from '@cskefu/shared-ui'
import { MenuOption, NMenu } from 'naive-ui'
import { ref } from 'vue'

const menuOptions: MenuOption[] = []
const collapsed = ref<boolean>(false)
</script>
<template>
  <MenusLayout v-model:collapsed="collapsed">
    <n-menu
      :options="menuOptions"
      :collapsed-width="64"
      :collapsed-icon-size="24"
    />
  </MenusLayout>
</template>
