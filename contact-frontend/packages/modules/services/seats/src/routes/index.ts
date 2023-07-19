import { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAME } from '@cskefu/models'

import Layout from '../layouts/MenusLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/seats',
    name: 'seats',
    component: Layout,
    redirect: '/seats/index',
    children: [
      {
        path: 'index',
        name: ROUTE_NAME.SEATS_INDEX,
        component: () => import('../views/HomeView.vue'),
      },
    ],
  },
]

export default routes
