<script setup lang="ts">
import { NButton, NDivider } from 'naive-ui'
</script>
<template>
  <div class="pt-2 flex flex-col">
    <span class="text-gray-500">原生</span>
    <n-button>用户名(默认)</n-button>
    <n-button disabled>邮箱</n-button>
    <n-button disabled>电话</n-button>
    <span class="text-gray-500">其他</span>
    <n-divider title-placement="left"> 国际 </n-divider>
    <n-button disabled>MicroSoft</n-button>
    <n-button disabled>Google</n-button>
    <n-button disabled>Apple</n-button>
    <n-button disabled>Facebook</n-button>
    <n-button disabled>Twitter</n-button>
    <n-button disabled>GitHub</n-button>
    <n-button disabled>Steam</n-button>
    <n-divider title-placement="left"> 国内 </n-divider>
    <n-button disabled>微信</n-button>
    <n-button disabled>微博</n-button>
    <n-button disabled>QQ</n-button>
    <n-button disabled>淘宝</n-button>
    <n-button disabled>钉钉</n-button>
    <n-button disabled>飞书</n-button>
    <n-button disabled>企业微信</n-button>
    <span class="text-gray-500">定制</span>
    <n-button disabled>OpenID Connect</n-button>
    <n-button disabled>SAML</n-button>
  </div>
</template>
