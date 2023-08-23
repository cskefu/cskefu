import { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAME } from '@cskefu/models'

import Layout from '../layouts/MenusLayout.vue'
import {
  AlbumsOutline,
  AnalyticsOutline,
  ChatbubblesOutline,
  DocumentAttachOutline,
  HardwareChip,
} from '@vicons/ionicons5'

const routes: RouteRecordRaw[] = [
  {
    path: '/agents',
    name: ROUTE_NAME.AGENTS_INDEX,
    component: Layout,
    redirect: '/agents/index',
    children: [
      {
        path: 'index',
        name: ROUTE_NAME.AGENTS_DASHBOARD_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '坐席看板',
          requiresAuth: true,
          icon: AnalyticsOutline,
        },
      },
      {
        path: 'chat-manage',
        name: ROUTE_NAME.AGENTS_CHATMANAGE_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '会话管理',
          requiresAuth: true,
          icon: ChatbubblesOutline,
        },
      },
      {
        path: 'leave-message',
        name: ROUTE_NAME.AGENTS_LEAVEMANAGE_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '留言管理',
          requiresAuth: true,
          icon: DocumentAttachOutline,
        },
      },
      {
        path: 'agents-manage',
        name: ROUTE_NAME.AGENTS_AGENTSMANAGE_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '坐席管理',
          requiresAuth: true,
          icon: AlbumsOutline,
        },
      },
      {
        path: 'robot',
        name: ROUTE_NAME.AGENTS_ROBOT_INDEX,
        meta: {
          title: '机器人管理',
          icon: HardwareChip,
          requiresAuth: false,
        },
        children: [
          {
            path: 'dashboard',
            name: ROUTE_NAME.AGENTS_ROBOT_DASHBOARD_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '数据统计',
              requiresAuth: true,
            },
          },
          {
            path: 'manage',
            name: ROUTE_NAME.AGENTS_ROBOT_MANAGE_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '机器人管理',
              requiresAuth: true,
            },
          },
          {
            path: 'setting',
            name: ROUTE_NAME.AGENTS_ROBOT_SETTING_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '机器人设置',
              requiresAuth: true,
            },
          },
        ],
      },
    ],
  },
]

export default routes
