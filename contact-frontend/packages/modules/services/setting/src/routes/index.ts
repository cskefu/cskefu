import { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAME } from '@cskefu/models'

import Layout from '../layouts/MenusLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/setting',
    name: 'setting',
    component: Layout,
    redirect: { name: ROUTE_NAME.SETTING_INDEX },
    children: [
      {
        path: 'index',
        name: ROUTE_NAME.SETTING_INDEX,
        component: () => import('../views/HomeView.vue'),
      },
    ],
  },
]

export default routes
