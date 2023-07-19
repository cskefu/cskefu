import { RouteRecordRaw } from 'vue-router'

import { IndexLayout } from '@cskefu/shared-ui'
import { ROUTE_NAME } from '@cskefu/models'

const routes: RouteRecordRaw[] = [
  {
    path: '/dashboard',
    name: 'dashboard',
    component: IndexLayout,
    redirect: { name: ROUTE_NAME.DASHBOARD_INDEX },
    children: [
      {
        path: 'index',
        name: ROUTE_NAME.DASHBOARD_INDEX,
        component: () => import('../views/DashboardView.vue'),
        meta: {
          title: '数据看板',
          auth: true,
        },
      },
    ],
  },
]

export default routes
