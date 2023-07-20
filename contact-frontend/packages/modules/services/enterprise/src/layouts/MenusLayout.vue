<script setup lang="ts">
import { ROUTE_NAME } from '@cskefu/models'
import { MenusLayout } from '@cskefu/shared-ui'
import { MenuOption, NMenu } from 'naive-ui'

const menuOptions: MenuOption[] = [
  {
    label: '企业服务',
    key: ROUTE_NAME.ENTERPRISE_INDEX,
  },
  {
    label: '客服设置',
    key: ROUTE_NAME.ENTERPRISE_KEFU_INDEX,
  },
  {
    label: '渠道接入',
    key: ROUTE_NAME.ENTERPRISE_CHANNEL_INDEX,
  },
  {
    label: '知识库',
    key: ROUTE_NAME.ENTERPRISE_KNOWLEDGE_INDEX,
  },
  {
    label: '质检',
    key: ROUTE_NAME.ENTERPRISE_KNOWLEDGE_INDEX,
  },
  {
    label: '账号管理',
    children: [
      {
        label: '账号管理',
        key: ROUTE_NAME.ENTERPRISE_USERS_INDEX,
      },
      {
        label: '角色管理',
        key: ROUTE_NAME.ENTERPRISE_ROLES_INDEX,
      },
      {
        label: '组织架构',
        key: ROUTE_NAME.ENTERPRISE_ROLES_INDEX,
      },
    ],
  },
  {
    label: '功能设置',
    children: [
      {
        label: '对话设置',
        key: ROUTE_NAME.ENTERPRISE_CHANNEL_INDEX,
      },
      {
        label: '客户设置',
        key: ROUTE_NAME.ENTERPRISE_CHANNEL_INDEX,
      },
      {
        label: '工单设置',
        key: ROUTE_NAME.ENTERPRISE_CHANNEL_INDEX,
      },
      {
        label: '留言设置',
        key: ROUTE_NAME.ENTERPRISE_CHANNEL_INDEX,
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
