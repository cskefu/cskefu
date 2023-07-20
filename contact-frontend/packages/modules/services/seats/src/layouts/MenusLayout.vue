<script setup lang="ts">
import { ROUTE_NAME } from '@cskefu/models'
import { MenusLayout } from '@cskefu/shared-ui'
import { MenuOption, NMenu } from 'naive-ui'

const menuOptions: MenuOption[] = [
  {
    label: '坐席看板',
    key: ROUTE_NAME.ENTERPRISE_INDEX,
  },
  {
    label: '会话管理',
    key: ROUTE_NAME.ENTERPRISE_INDEX,
  },
  {
    label: '留言管理',
    key: ROUTE_NAME.ENTERPRISE_INDEX,
  },
  {
    label: '坐席管理',
    key: ROUTE_NAME.ENTERPRISE_INDEX,
  },
  {
    label: '机器人管理',
    children: [
      {
        label: '数据统计',
        key: ROUTE_NAME.ENTERPRISE_INDEX,
      },
      {
        label: '机器人管理',
        key: ROUTE_NAME.ENTERPRISE_INDEX,
      },
      {
        label: '机器人设置',
        key: ROUTE_NAME.ENTERPRISE_INDEX,
      },
    ],
  },
]
</script>
<template>
  <MenusLayout>
    <n-menu :options="menuOptions" />
  </MenusLayout>
</template>
