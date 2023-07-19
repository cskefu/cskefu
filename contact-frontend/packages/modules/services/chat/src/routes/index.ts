import { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAME } from '@cskefu/models'

import Layout from '../layouts/MenusLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/chat',
    name: 'chat',
    component: Layout,
    redirect: '/chat/index',
    children: [
      {
        path: 'index',
        name: ROUTE_NAME.CHAT_INDEX,
        component: () => import('../views/HomeView.vue'),
      },
    ],
  },
]

export default routes
