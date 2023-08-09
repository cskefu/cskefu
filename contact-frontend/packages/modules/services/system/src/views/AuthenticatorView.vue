<script setup lang="ts">
import { ROUTE_NAME } from '@cskefu/models'
import { NTabs, NTab } from 'naive-ui'
</script>
<template>
  <div class="p-4">
    <n-tabs
      type="line"
      :bar-width="32"
      :on-update:value="
        (value) => {
          $router.push({
            name: value as ROUTE_NAME,
          })
        }
      "
    >
      <n-tab :name="ROUTE_NAME.SYSTEM_AUTHENTICATOR_USERS_INDEX"> 用户 </n-tab>
      <n-tab :name="ROUTE_NAME.SYSTEM_AUTHENTICATOR_METHODS_INDEX">
        登录提供方
      </n-tab>
      <n-tab :name="ROUTE_NAME.SYSTEM_AUTHENTICATOR_TEMPLATE_INDEX" disabled>
        模板
      </n-tab>
      <n-tab :name="ROUTE_NAME.SYSTEM_AUTHENTICATOR_SETTING_INDEX">
        设置
      </n-tab>
    </n-tabs>
    <div>
      <router-view></router-view>
    </div>
  </div>
</template>
