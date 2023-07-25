<script setup lang="ts">
import {
  NLayout,
  NLayoutContent,
  NLayoutSider,
  NPageHeader,
  NSpace,
  NButton,
  NDropdown,
  NWatermark,
  NIcon,
} from 'naive-ui'

defineProps({
  isWatermarkMode: {
    type: Boolean,
    default: false,
  },
  watermarkLabel: {
    type: String,
    default: '',
  },
  collapsed: {
    type: Boolean,
    default: false,
  },
  pageTitle: {
    type: String,
    default: '',
    required: false,
  },
  pageSubtitle: {
    type: String,
    default: '',
    required: false,
  },
  pageIcon: {
    type: Object,
    default: null,
    required: false,
  },
})

defineEmits(['update:collapsed'])
</script>
<template>
  <n-layout has-sider class="h-full">
    <n-watermark
      v-if="isWatermarkMode"
      :content="watermarkLabel"
      cross
      fullscreen
      :font-size="16"
      :line-height="16"
      :width="384"
      :height="384"
      :x-offset="12"
      :y-offset="60"
      :rotate="-15"
    />
    <n-layout-sider
      :collapsed-width="64"
      :width="280"
      :collapsed="collapsed"
      bordered
      show-trigger="bar"
      collapse-mode="width"
      @collapse="$emit('update:collapsed', true)"
      @expand="$emit('update:collapsed', false)"
    >
      <slot></slot>
    </n-layout-sider>
    <n-layout-content
      content-style="display: flex; flex-direction: column;width: 100%;height:100%;padding: 10px"
    >
      <n-page-header :subtitle="pageSubtitle">
        <template #avatar>
          <n-icon size="large" :component="pageIcon" />
        </template>
        <template #title>
          <a style="text-decoration: none; color: inherit">{{ pageTitle }}</a>
        </template>
        <template #extra>
          <n-space>
            <n-button size="tiny">Button</n-button>
            <n-dropdown placement="bottom-start">
              <n-button :bordered="false" style="padding: 0 4px">
                ···
              </n-button>
            </n-dropdown>
          </n-space>
        </template>
      </n-page-header>
      <div class="h-full">
        <router-view></router-view>
      </div>
    </n-layout-content>
  </n-layout>
</template>
