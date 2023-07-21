<script setup lang="ts">
import { NMenu } from 'naive-ui'
import { NBadge, MenuOption } from 'naive-ui'
import { h } from 'vue'

function renderMenuLabel(option: MenuOption) {
  if (option.comingSoon) {
    return h(NBadge, { value: 'coming soon', offset: [50, 10] }, () => [
      h('slot', { class: 'text-gray-900' }, option.label as string),
    ])
  }
  return option.label as string
}
</script>
<template>
  <n-menu :render-label="renderMenuLabel" />
</template>
