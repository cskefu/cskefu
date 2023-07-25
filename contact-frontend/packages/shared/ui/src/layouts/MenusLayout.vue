<script setup lang="ts">
import {
  NLayout,
  NLayoutContent,
  NLayoutSider,
  NPageHeader,
  NBreadcrumb,
  NBreadcrumbItem,
  NAvatar,
  NSpace,
  NButton,
  NDropdown,
  NWatermark,
} from 'naive-ui'

defineProps({
  showWatermark: {
    type: Boolean,
    default: true,
  },
  collapsed: {
    type: Boolean,
    default: false,
  },
})

defineEmits(['update:collapsed'])
</script>
<template>
  <n-layout has-sider class="h-full">
    <n-watermark
      v-if="showWatermark"
      content="大家艰苦一下，一切都会有的"
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
      <n-page-header subtitle="subtitle">
        <template #header>
          <n-breadcrumb>
            <n-breadcrumb-item>Github</n-breadcrumb-item>
            <n-breadcrumb-item>CSKEFU</n-breadcrumb-item>
            <n-breadcrumb-item>春松客服</n-breadcrumb-item>
          </n-breadcrumb>
        </template>
        <template #avatar>
          <n-avatar
            src="https://avatars.githubusercontent.com/u/16386583?v=4"
          />
        </template>
        <template #title>
          <a style="text-decoration: none; color: inherit">Title</a>
        </template>
        <template #extra>
          <n-space>
            <n-button>Button</n-button>
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
