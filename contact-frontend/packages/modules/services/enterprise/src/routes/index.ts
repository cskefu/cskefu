import { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAME } from '@cskefu/models'

import Layout from '../layouts/MenusLayout.vue'
import {
  BusinessOutline,
  GitNetworkOutline,
  LibraryOutline,
  BagCheckOutline,
  PeopleOutline,
  OptionsOutline,
} from '@vicons/ionicons5'

const routes: RouteRecordRaw[] = [
  {
    path: '/enterprise',
    name: ROUTE_NAME.ENTERPRISE_INDEX,
    component: Layout,
    redirect: '/enterprise/index',
    children: [
      {
        path: 'index',
        name: ROUTE_NAME.ENTERPRISE_SERVICES_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '企业服务',
          requiresAuth: true,
          icon: BusinessOutline,
        },
      },
      {
        path: 'channel',
        name: ROUTE_NAME.ENTERPRISE_CHANNEL_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '渠道接入',
          requiresAuth: true,
          icon: GitNetworkOutline,
        },
      },
      {
        path: 'knowledge',
        name: ROUTE_NAME.ENTERPRISE_KNOWLEDGE_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '知识库',
          requiresAuth: true,
          icon: LibraryOutline,
        },
      },
      {
        path: 'quality',
        name: ROUTE_NAME.ENTERPRISE_QUALITY_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '质检',
          requiresAuth: true,
          icon: BagCheckOutline,
        },
      },
      {
        path: 'ou',
        name: ROUTE_NAME.ENTERPRISE_ADMIN_INDEX,
        meta: {
          title: '组织/成员',
          icon: PeopleOutline,
        },
        children: [
          {
            path: 'users',
            name: ROUTE_NAME.ENTERPRISE_USERS_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '账号管理',
              requiresAuth: true,
            },
          },
          {
            path: 'roles',
            name: ROUTE_NAME.ENTERPRISE_ROLES_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '角色管理',
              requiresAuth: true,
            },
          },
          {
            path: 'organization',
            name: ROUTE_NAME.ENTERPRISE_ORGANIZATIONS_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '组织架构',
              requiresAuth: true,
            },
          },
        ],
      },
      {
        path: 'feature',
        name: ROUTE_NAME.ENTERPRISE_FEATURE_INDEX,
        meta: {
          title: '功能设置',
          icon: OptionsOutline,
        },
        children: [
          {
            path: 'index',
            name: ROUTE_NAME.ENTERPRISE_CHAT_SETTING_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '对话设置',
              requiresAuth: true,
            },
          },
          {
            path: 'index',
            name: ROUTE_NAME.ENTERPRISE_CUSTOMER_SETTING_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '客户设置',
              requiresAuth: true,
            },
          },
          {
            path: 'index',
            name: ROUTE_NAME.ENTERPRISE_WORKORDER_SETTING_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '工单设置',
              requiresAuth: true,
            },
          },
          {
            path: 'index',
            name: ROUTE_NAME.ENTERPRISE_LEAVEMESSAGE_SETTING_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '留言设置',
              requiresAuth: true,
            },
          },
        ],
      },
    ],
  },
]

export default routes
