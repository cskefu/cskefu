<script setup lang="ts">
import { NLayout, NLayoutContent, NLayoutSider } from 'naive-ui'
</script>
<template>
  <n-layout has-sider class="h-full">
    <n-layout-sider bordered>
      <slot></slot>
    </n-layout-sider>
    <n-layout-content content-style="padding: 10px">
      <router-view></router-view>
    </n-layout-content>
  </n-layout>
</template>
