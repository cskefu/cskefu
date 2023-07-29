<script setup lang="ts">
import { NTabs, NTab } from 'naive-ui'
</script>
<template>
  <div class="p-4">
    <n-tabs type="line" :bar-width="32">
      <n-tab name="users"> 用户 </n-tab>
      <n-tab name="social"> 登录方式 </n-tab>
      <n-tab name="template"> 模板 </n-tab>
      <n-tab name="dashboard"> 使用情况 </n-tab>
      <n-tab name="setting"> 设置 </n-tab>
    </n-tabs>
  </div>
</template>
