<script setup lang="ts">
import { NCard, NGrid, NGi } from 'naive-ui'
</script>
<template>
  <div class="p-4">
    <n-grid cols="5" :x-gap="12" :y-gap="8">
      <n-gi :span="3">
        <n-card title="小卡片" size="small"> 卡片内容 </n-card>
      </n-gi>
      <n-gi>
        <n-card title="小卡片" size="small"> 卡片内容 </n-card>
      </n-gi>
      <n-gi>
        <n-card title="小卡片" size="small"> 卡片内容 </n-card>
      </n-gi>
      <n-gi>
        <n-card title="小卡片" size="small"> 卡片内容 </n-card>
      </n-gi>
      <n-gi>
        <n-card title="小卡片" size="small"> 卡片内容 </n-card>
      </n-gi>
      <n-gi>
        <n-card title="小卡片" size="small"> 卡片内容 </n-card>
      </n-gi>
      <n-gi>
        <n-card title="小卡片" size="small"> 卡片内容 </n-card>
      </n-gi>
    </n-grid>
  </div>
</template>
