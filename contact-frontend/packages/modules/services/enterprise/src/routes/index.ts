import { RouteRecordRaw } from 'vue-router'
import { ROUTE_NAME } from '@cskefu/models'

import Layout from '../layouts/MenusLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/enterprise',
    name: 'enterprise',
    component: Layout,
    redirect: '/enterprise/index',
    children: [
      {
        path: 'index',
        name: ROUTE_NAME.ENTERPRISE_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '企业服务',
          isAuth: true,
          icon: 'el-icon-setting',
        },
      },
      {
        path: 'chat',
        name: ROUTE_NAME.ENTERPRISE_KEFU_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '客服入口设置',
          isAuth: true,
          icon: 'el-icon-setting',
        },
      },
      {
        path: 'channel',
        name: ROUTE_NAME.ENTERPRISE_CHANNEL_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '渠道接入',
          isAuth: true,
          icon: 'el-icon-setting',
        },
      },
      {
        path: 'knowledge',
        name: ROUTE_NAME.ENTERPRISE_KNOWLEDGE_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '知识库',
          isAuth: true,
          icon: 'el-icon-setting',
        },
      },
      {
        path: 'quality',
        name: ROUTE_NAME.ENTERPRISE_QUALITY_INDEX,
        component: () => import('../views/HomeView.vue'),
        meta: {
          title: '质检',
          isAuth: true,
          icon: 'el-icon-setting',
        },
      },
      {
        path: 'ou',
        name: ROUTE_NAME.ENTERPRISE_ADMIN_INDEX,
        meta: {
          title: '组织/成员',
        },
        children: [
          {
            path: 'users',
            name: ROUTE_NAME.ENTERPRISE_USERS_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '账号管理',
              isAuth: true,
              icon: 'el-icon-setting',
            },
          },
          {
            path: 'roles',
            name: ROUTE_NAME.ENTERPRISE_ROLES_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '角色管理',
              isAuth: true,
              icon: 'el-icon-setting',
            },
          },
          {
            path: 'organization',
            name: ROUTE_NAME.ENTERPRISE_ORGANIZATIONS_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '组织架构',
              isAuth: true,
              icon: 'el-icon-setting',
            },
          },
        ],
      },
      {
        path: 'feature',
        name: ROUTE_NAME.ENTERPRISE_FEATURE_INDEX,
        meta: {
          title: '功能设置',
        },
        children: [
          {
            path: 'index',
            name: ROUTE_NAME.ENTERPRISE_CHAT_SETTING_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '对话设置',
              isAuth: true,
              icon: 'el-icon-setting',
            },
          },
          {
            path: 'index',
            name: ROUTE_NAME.ENTERPRISE_CUSTOMER_SETTING_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '客户设置',
              isAuth: true,
              icon: 'el-icon-setting',
            },
          },
          {
            path: 'index',
            name: ROUTE_NAME.ENTERPRISE_WORKORDER_SETTING_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '工单设置',
              isAuth: true,
              icon: 'el-icon-setting',
            },
          },
          {
            path: 'index',
            name: ROUTE_NAME.ENTERPRISE_LEAVEMESSAGE_SETTING_INDEX,
            component: () => import('../views/HomeView.vue'),
            meta: {
              title: '留言设置',
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
