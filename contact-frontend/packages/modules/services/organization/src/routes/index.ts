import { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAME } from '@cskefu/models'

import Layout from '../layouts/MenusLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/organization',
    name: ROUTE_NAME.ORGANIZATION_INDEX,
    component: Layout,
    redirect: { name: ROUTE_NAME.ORGANIZATION_HOME_INDEX },
    children: [
      {
        path: 'index',
        name: ROUTE_NAME.ORGANIZATION_HOME_INDEX,
        component: () => import('../views/HomeView.vue'),
      },
    ],
  },
]

export default routes
