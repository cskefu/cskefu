import { RouteRecordRaw, createRouter, createWebHashHistory } from 'vue-router'

import { ROUTE_NAME } from '@cskefu/models'

import { routes as authRoutes } from '@cskefu/services-auth'
import { routes as dashboardRoutes } from '@cskefu/services-dashboard'
import { routes as settingRoutes } from '@cskefu/services-setting'
import { routes as systemRoutes } from '@cskefu/services-system'
import { routes as seatsRoutes } from '@cskefu/services-seats'
import { routes as organizationRoutes } from '@cskefu/services-organization'
import { routes as chatRoutes } from '@cskefu/services-chat'
import { routes as workOrderRoutes } from '@cskefu/services-work-order'
import { routes as enterpriseRoutes } from '@cskefu/services-enterprise'

import NavLayout from '../layouts/NavLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('../views/HomeView.vue'),
  },
  {
    path: '/login',
    redirect: { name: ROUTE_NAME.AUTH_LOGIN },
  },
  {
    path: '/index',
    component: NavLayout,
    children: [
      ...dashboardRoutes,
      ...chatRoutes,
      ...workOrderRoutes,
      ...seatsRoutes,
      ...organizationRoutes,
      ...settingRoutes,
      ...systemRoutes,
      ...enterpriseRoutes,
    ],
  },
  ...authRoutes,
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

export default router
