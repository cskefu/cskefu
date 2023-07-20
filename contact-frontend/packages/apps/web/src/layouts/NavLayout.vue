<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { Nav } from '@cskefu/shared-ui'
import { ROUTE_NAME } from '@cskefu/models'

import {
  NSwitch,
  NIcon,
  NAlert,
  NPopselect,
  NDrawer,
  NDrawerContent,
  NBadge,
} from 'naive-ui'
import {
  SunnyOutline,
  Moon,
  Earth,
  NotificationsOutline,
} from '@vicons/ionicons5'

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
  { label: '意见反馈', value: 'https://github.com/cskefu/cskefu/issues' },
  { label: '关于我们', value: 'https://github.com/cskefu' },
  { label: '退出登录', value: 'logout' },
]

const current = ref<string>(ROUTE_NAME.DASHBOARD_INDEX)
const path = window.location.pathname.split('/')[1]
switch (path) {
  case 'dashboard':
    current.value = ROUTE_NAME.DASHBOARD_INDEX
    break
  case 'chat':
    current.value = ROUTE_NAME.CHAT_INDEX
    break
  case 'work-order':
    current.value = ROUTE_NAME.WORK_ORDER_INDEX
    break
  case 'seats':
    current.value = ROUTE_NAME.SEATS_INDEX
    break
  case 'organization':
    current.value = ROUTE_NAME.ORGANIZATION_INDEX
    break
  case 'setting':
    current.value = ROUTE_NAME.SETTING_INDEX
    break
  case 'system':
    current.value = ROUTE_NAME.SYSTEM_INDEX
    break
  case 'profile':
    current.value = ROUTE_NAME.PROFILE_INDEX
    break
  default:
    current.value = ROUTE_NAME.PAGE_NOT_FOUND
}
const router = useRouter()

function handleClickNav(name: string) {
  if (name.startsWith('https://') || name.startsWith('http://')) {
    window.open(name, '_blank')
    return
  }
  if (name === 'logout') {
    return
  }
  router.push({ name })
}

const active = ref(false)
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
      @update:current="handleClickNav"
    >
      <template #production>
        <div class="flex items-center space-x-2">
          <div class="text-sm">XX公司官网</div>
          <span>-</span>
          <div class="text-sm">默认客服部门</div>
        </div>
      </template>
      <RouterLink class="text-sm text-green-600" to="/index">
        使用教程
      </RouterLink>
      <n-badge :value="99" :max="9" class="text-sm">
        <n-icon
          class="flex items-center cursor-pointer"
          :component="NotificationsOutline"
          size="25"
          @click="active = !active"
        />
      </n-badge>
      <template #dropMenuAppend>
        <div class="flex justify-between items-center">
          <div class="flex justify-between items-center space-x-2">
            <n-icon :component="Earth" />
            <n-popselect
              v-model:value="$i18n.locale"
              :options="[
                { label: 'English', value: 'en-US' },
                { label: 'Chinese', value: 'zh-CN' },
              ]"
              trigger="hover"
            >
              <div class="hover: text-green-600 hover:underline cursor-pointer">
                {{ $t($i18n.locale) }}
              </div>
            </n-popselect>
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
    <div id="drawer-target" class="grow relative overflow-hidden">
      <router-view></router-view>
    </div>
    <n-drawer
      v-model:show="active"
      :width="502"
      :height="200"
      placement="right"
      :show-mask="false"
      :trap-focus="false"
      :block-scroll="false"
      to="#drawer-target"
    >
      <n-drawer-content title="通知中心"> 通知中心列表卡片 </n-drawer-content>
    </n-drawer>
  </div>
</template>
