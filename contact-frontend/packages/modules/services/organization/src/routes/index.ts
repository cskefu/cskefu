import { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAME } from '@cskefu/models'

import Layout from '../layouts/MenusLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/organization',
    name: 'organization',
    component: Layout,
    redirect: '/organization/index',
    children: [
      {
        path: 'index',
        name: ROUTE_NAME.ORGANIZATION_INDEX,
        component: () => import('../views/HomeView.vue'),
      },
    ],
  },
]

export default routes
