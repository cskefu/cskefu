import { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAME } from '@cskefu/models'

import Layout from '../layouts/MenusLayout.vue'
import {
  BeakerOutline,
  CallOutline,
  ChatboxEllipsesOutline,
  ClipboardOutline,
  FootballOutline,
  IdCardOutline,
  InfiniteSharp,
  LayersOutline,
  NotificationsOutline,
  PlanetOutline,
} from '@vicons/ionicons5'

const routes: RouteRecordRaw[] = [
  {
    path: '/system',
    name: ROUTE_NAME.SYSTEM_INDEX,
    component: Layout,
    redirect: '/system/index',
    children: [
      {
        path: 'index',
        name: ROUTE_NAME.SYSTEM_INFO_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '系统信息',
          requiresAuth: true,
          icon: InfiniteSharp,
        },
      },
      {
        path: 'social',
        name: ROUTE_NAME.SYSTEM_SOCIAL_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '认证登录',
          requiresAuth: true,
          icon: IdCardOutline,
        },
      },
      {
        path: 'oss',
        name: ROUTE_NAME.SYSTEM_OSS_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '对象存储设置',
          requiresAuth: true,
          icon: LayersOutline,
        },
      },
      {
        path: 'cdn',
        name: ROUTE_NAME.SYSTEM_CDN_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: 'CDN 设置',
          requiresAuth: true,
          icon: PlanetOutline,
        },
      },
      {
        path: 'sms',
        name: ROUTE_NAME.SYSTEM_SMS_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '短信平台设置',
          requiresAuth: true,
          icon: ChatboxEllipsesOutline,
          comingSoon: true,
          disabled: true,
        },
      },
      {
        path: 'call',
        name: ROUTE_NAME.SYSTEM_CALL_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '呼叫平台设置',
          requiresAuth: true,
          icon: CallOutline,
          comingSoon: true,
          disabled: true,
        },
      },
      {
        path: 'gpt',
        name: ROUTE_NAME.SYSTEM_GPT_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: 'GPT 设置',
          requiresAuth: true,
          icon: BeakerOutline,
          comingSoon: true,
          disabled: true,
        },
      },
      {
        path: 'source',
        name: ROUTE_NAME.SYSTEM_SOURCE_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '元数据',
          requiresAuth: true,
          icon: FootballOutline,
        },
      },
      {
        path: 'notification',
        name: ROUTE_NAME.SYSTEM_NOTIFICATION_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '通知设置',
          requiresAuth: true,
          icon: NotificationsOutline,
        },
      },
      {
        path: 'notification',
        name: ROUTE_NAME.SYSTEM_LOG_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '日志查看',
          requiresAuth: true,
          icon: ClipboardOutline,
        },
      },
    ],
  },
]

export default routes
