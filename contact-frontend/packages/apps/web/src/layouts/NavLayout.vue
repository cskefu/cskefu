<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, RouterView, useRouter } from 'vue-router'

import { Nav } from '@cskefu/shared-ui'

import { NSwitch, NIcon, NAlert } from 'naive-ui'
import { SunnyOutline, Moon, Earth } from '@vicons/ionicons5'
import { ROUTE_NAME } from '@cskefu/models'

const navigations = [
  { label: '首页', value: ROUTE_NAME.DASHBOARD_INDEX },
  { label: '对话', value: ROUTE_NAME.CHAT_INDEX },
  { label: '工单', value: ROUTE_NAME.WORK_ORDER_INDEX },
  { label: '坐席', value: ROUTE_NAME.SEATS_INDEX },
  { label: '组织', value: ROUTE_NAME.ORGANIZATION_INDEX },
  { label: '设置', value: ROUTE_NAME.SETTING_INDEX },
]

const dropdownMenus = [
  { label: '个人中心', value: ROUTE_NAME.PROFILE_INDEX },
  { label: '企业设置', value: ROUTE_NAME.ENTERPRISE_INDEX },
  { label: '系统设置', value: ROUTE_NAME.SYSTEM_INDEX },
  { label: '意见反馈', value: 'reedback' },
  { label: '关于我们', value: 'about' },
  { label: '退出登录', value: 'logout' },
]

// TODO 根据 url 设置当前选中的导航
const current = ref<string>(ROUTE_NAME.DASHBOARD_INDEX)
const router = useRouter()
</script>
<template>
  <div class="flex flex-col h-screen bg-gray-100">
    <n-alert type="warning" closable class="text-center">
      欢迎使用“春松客服”系统，2024-01-01
      到期（剩余30天），如继续试用请联系管理员
    </n-alert>
    <Nav
      v-model:current="current"
      avatar-url="https://avatars.githubusercontent.com/u/499270?v=4"
      :navigations="navigations"
      :dropdown-menus="dropdownMenus"
      @update:current="
        (name: string) => {
          router.push({ name })
        }
      "
    >
      <RouterLink class="text-sm text-green-600" to="/index">
        使用指南
      </RouterLink>
      <template #dropMenuAppend>
        <div class="flex justify-between items-center">
          <div class="flex justify-between items-center space-x-2">
            <n-icon :component="Earth" />
            <span>中文</span>
          </div>
          <n-switch size="medium">
            <template #checked-icon>
              <n-icon :component="Moon" />
            </template>
            <template #unchecked-icon>
              <n-icon :component="SunnyOutline" />
            </template>
          </n-switch>
        </div>
      </template>
    </Nav>
    <div class="grow overflow-hidden">
      <router-view></router-view>
    </div>
  </div>
</template>
