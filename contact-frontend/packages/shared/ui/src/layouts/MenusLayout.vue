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
</script>
<template>
  <n-layout has-sider class="h-full">
    <n-layout-sider bordered show-trigger="bar" collapse-mode="transform">
      <slot></slot>
    </n-layout-sider>
    <n-layout-content content-style="padding: 10px">
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
        <n-watermark
          content="核心机密"
          cross
          selectable
          :font-size="23"
          :line-height="16"
          :width="192"
          :height="128"
          :x-offset="12"
          :y-offset="28"
          :rotate="-15"
        >
          <router-view></router-view>
        </n-watermark>
      </n-page-header>
    </n-layout-content>
  </n-layout>
</template>
