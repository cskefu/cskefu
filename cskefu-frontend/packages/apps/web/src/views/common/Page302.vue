<script setup lang="ts">
import { NButton } from 'naive-ui'
</script>
<template>
  <div class="flex justify-center items-center h-full">
    <div class="h-1/2 flex flex-col items-center justify-center">
      <span class="text-xl">即将离开，跳转至</span>
      <n-button @click="() => $router.go(-1)">返回</n-button>
    </div>
  </div>
</template>
