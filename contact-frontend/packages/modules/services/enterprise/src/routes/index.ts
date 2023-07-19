import { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAME } from '@cskefu/models'

import Layout from '../layouts/MenusLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/enterprise',
    name: 'enterprise',
    component: Layout,
    redirect: '/enterprise/index',
    children: [
      {
        path: 'index',
        name: ROUTE_NAME.ENTERPRISE_INDEX,
        component: () => import('../views/HomeView.vue'),
      },
    ],
  },
]

export default routes
