import { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAME } from '@cskefu/models'

import Layout from '../layouts/MenusLayout.vue'
import {
  AlbumsOutline,
  AnalyticsOutline,
  ChatbubblesOutline,
  DocumentAttachOutline,
  BarChartOutline,
  SettingsOutline,
  HardwareChip,
  GridOutline,
} from '@vicons/ionicons5'

const routes: RouteRecordRaw[] = [
  {
    path: '/seats',
    name: 'seats',
    component: Layout,
    redirect: '/seats/index',
    children: [
      {
        path: 'index',
        name: ROUTE_NAME.SEATS_DASHBOARD_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '坐席看板',
          requiresAuth: true,
          icon: AnalyticsOutline,
        },
      },
      {
        path: 'index',
        name: ROUTE_NAME.SEATS_CHATMANAGE_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '会话管理',
          requiresAuth: true,
          icon: ChatbubblesOutline,
        },
      },
      {
        path: 'index',
        name: ROUTE_NAME.SEATS_LEAVEMANAGE_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '留言管理',
          requiresAuth: true,
          icon: DocumentAttachOutline,
        },
      },
      {
        path: 'index',
        name: ROUTE_NAME.SEATS_SEATSMANAGE_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '坐席管理',
          requiresAuth: true,
          icon: AlbumsOutline,
        },
      },
      {
        path: 'index',
        name: ROUTE_NAME.SEATS_ROBOT_INDEX,
        meta: {
          title: '机器人管理',
          icon: HardwareChip,
        },
        children: [
          {
            path: 'index',
            name: ROUTE_NAME.SEATS_ROBOT_DASHBOARD_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '数据统计',
              requiresAuth: true,
              icon: BarChartOutline,
            },
          },
          {
            path: 'index',
            name: ROUTE_NAME.SEATS_ROBOT_MANAGE_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '机器人管理',
              requiresAuth: true,
              icon: GridOutline,
            },
          },
          {
            path: 'index',
            name: ROUTE_NAME.SEATS_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '机器人设置',
              requiresAuth: true,
              icon: SettingsOutline,
            },
          },
        ],
      },
    ],
  },
]

export default routes
