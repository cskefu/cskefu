import { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAME } from '@cskefu/models'

import Layout from '../layouts/MenusLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/work-order',
    name: 'work-order',
    component: Layout,
    redirect: { name: ROUTE_NAME.WORK_ORDER_INDEX },
    children: [
      {
        path: 'index',
        name: ROUTE_NAME.WORK_ORDER_INDEX,
        component: () => import('../views/HomeView.vue'),
      },
    ],
  },
]

export default routes
