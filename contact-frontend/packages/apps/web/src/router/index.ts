import { RouteRecordRaw, createRouter, createWebHistory } from 'vue-router'

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
      {
        path: 'profile',
        name: ROUTE_NAME.PROFILE_INDEX,
        component: () => import('../views/ProfileView.vue'),
      },
      ...dashboardRoutes,
      ...chatRoutes,
      ...workOrderRoutes,
      ...seatsRoutes,
      ...organizationRoutes,
      ...settingRoutes,
      ...systemRoutes,
      ...enterpriseRoutes,
      // {
      //   path: '302',
      //   name: ROUTE_NAME.PAGE_REDIRECT,
      //   component: () => import('../views/common/Page302.vue'),
      // },
      {
        path: '403',
        name: ROUTE_NAME.PAGE_FORBIDDEN,
        component: () => import('../views/common/Page403.vue'),
      },
      {
        path: '404',
        name: ROUTE_NAME.PAGE_NOT_FOUND,
        component: () => import('../views/common/Page404.vue'),
      },
      {
        path: '500',
        name: ROUTE_NAME.PAGE_SERVER_ERROR,
        component: () => import('../views/common/Page500.vue'),
      },
    ],
  },
  ...authRoutes,
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
