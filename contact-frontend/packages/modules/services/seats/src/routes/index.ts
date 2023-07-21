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
        name: ROUTE_NAME.SEATS_DASHBOARD_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '坐席看板',
          isAuth: true,
          icon: 'el-icon-setting',
        },
      },
      {
        path: 'index',
        name: ROUTE_NAME.SEATS_CHATMANAGE_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '会话管理',
          isAuth: true,
          icon: 'el-icon-setting',
        },
      },
      {
        path: 'index',
        name: ROUTE_NAME.SEATS_LEAVEMANAGE_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '留言管理',
          isAuth: true,
          icon: 'el-icon-setting',
        },
      },
      {
        path: 'index',
        name: ROUTE_NAME.SEATS_SEATSMANAGE_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '坐席管理',
          isAuth: true,
          icon: 'el-icon-setting',
        },
      },
      {
        path: 'index',
        name: ROUTE_NAME.SEATS_ROBOT_INDEX,
        meta: {
          title: '机器人管理',
        },
        children: [
          {
            path: 'index',
            name: ROUTE_NAME.SEATS_ROBOT_DASHBOARD_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '数据统计',
              isAuth: true,
              icon: 'el-icon-setting',
            },
          },
          {
            path: 'index',
            name: ROUTE_NAME.SEATS_ROBOT_MANAGE_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '机器人管理',
              isAuth: true,
              icon: 'el-icon-setting',
            },
          },
          {
            path: 'index',
            name: ROUTE_NAME.SEATS_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '机器人设置',
              isAuth: true,
              icon: 'el-icon-setting',
            },
          },
        ],
      },
    ],
  },
]

export default routes
