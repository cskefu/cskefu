<script setup lang="ts">
import { NTabs, NTab } from 'naive-ui'
</script>
<template>
  <div class="p-4">
    <n-tabs type="line" :bar-width="32">
      <n-tab name="meta-data"> 元数据 </n-tab>
      <n-tab name="dictionary"> 字典 </n-tab>
    </n-tabs>
  </div>
</template>
