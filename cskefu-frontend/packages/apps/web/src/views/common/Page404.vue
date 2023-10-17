<script setup lang="ts">
import { NButton } from 'naive-ui'
import bg from '@cskefu/assets//img/bg-404.svg'
</script>
<template>
  <div class="flex justify-center items-center h-full">
    <div class="h-1/2 flex flex-col items-center justify-center">
      <span class="text-xl">页面未找到</span>
      <img :src="bg" alt="404" class="h-full" />
      <n-button @click="() => $router.go(-1)">返回</n-button>
    </div>
  </div>
</template>
