import { RouteRecordRaw } from 'vue-router'

import { IndexLayout } from '@cskefu/shared-ui'
import { ROUTE_NAME } from '@cskefu/models'

const routes: RouteRecordRaw[] = [
  {
    path: '/auth',
    name: ROUTE_NAME.AUTH_INDEX,
    component: IndexLayout,
    redirect: { name: ROUTE_NAME.AUTH_LOGIN },
    children: [
      {
        path: 'login',
        name: ROUTE_NAME.AUTH_LOGIN,
        component: () => import('../views/LoginView.vue'),
        meta: {
          title: '登录',
          auth: false,
        },
      },
    ],
  },
]

export default routes
