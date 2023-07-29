<script setup lang="ts">
import { NTabs, NTab } from 'naive-ui'
</script>
<template>
  <div class="p-4">
    <n-tabs type="line" :bar-width="32">
      <n-tab name="users"> CDN 提供方 </n-tab>
      <n-tab name="dashboard"> 使用情况 </n-tab>
      <n-tab name="setting"> 设置 </n-tab>
    </n-tabs>
  </div>
</template>
