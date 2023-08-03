<script setup lang="ts">
import { NTabs, NTab } from 'naive-ui'
</script>
<template>
  <div class="p-4">
    <n-tabs type="line" :bar-width="32">
      <n-tab name="users"> 用户 </n-tab>
      <n-tab name="social"> 登录提供方 </n-tab>
      <n-tab name="template"> 模板 </n-tab>
      <n-tab name="setting"> 设置 </n-tab>
    </n-tabs>
    <div>
      <router-view></router-view>
    </div>
  </div>
</template>
