import { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAME } from '@cskefu/models'

import Layout from '../layouts/MenusLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/system',
    name: 'system',
    component: Layout,
    redirect: '/system/index',
    children: [
      {
        path: 'index',
        name: ROUTE_NAME.SYSTEM_INDEX,
        component: () => import('../views/HomeView.vue'),
      },
      {
        path: 'social',
        name: ROUTE_NAME.SYSTEM_SOCIAL_INDEX,
        component: () => import('../views/HomeView.vue'),
      },
      {
        path: 'oss',
        name: ROUTE_NAME.SYSTEM_OSS_INDEX,
        component: () => import('../views/HomeView.vue'),
      },
      {
        path: 'cdn',
        name: ROUTE_NAME.SYSTEM_CDN_INDEX,
        component: () => import('../views/HomeView.vue'),
      },
      {
        path: 'sms',
        name: ROUTE_NAME.SYSTEM_SMS_INDEX,
        component: () => import('../views/HomeView.vue'),
      },
      {
        path: 'call',
        name: ROUTE_NAME.SYSTEM_CALL_INDEX,
        component: () => import('../views/HomeView.vue'),
      },
      {
        path: 'gpt',
        name: ROUTE_NAME.SYSTEM_GPT_INDEX,
        component: () => import('../views/HomeView.vue'),
      },
      {
        path: 'source',
        name: ROUTE_NAME.SYSTEM_SOURCE_INDEX,
        component: () => import('../views/HomeView.vue'),
      },
      {
        path: 'notification',
        name: ROUTE_NAME.SYSTEM_NOTIFICATION_INDEX,
        component: () => import('../views/HomeView.vue'),
      },
    ],
  },
]

export default routes
