<script setup lang="ts">
import bgUrl from '@cskefu/assets/img/login-bg.svg'
import logoUrl from '@cskefu/assets/img/logo.png'

import { LogoWechat, LogoGithub } from '@vicons/ionicons5'
import { NInput, NButton, NDivider, NIcon } from 'naive-ui'
</script>
<template>
  <main class="w-full h-full m-auto flex row align-middle">
    <div
      class="hidden md:flex items-center justify-center w-1/2 h-full bg-gradient-to-t to-emerald-500 from-10% from-indigo-500 to-90%"
    >
      <img :src="bgUrl" alt="春松客服" class="w-full" />
    </div>
    <div
      class="flex flex-col justify-center items-center w-full h-full space-y-5 md:w-1/2"
    >
      <img :src="logoUrl" alt="春松客服" width="300" />
      <h1 class="text-xl">{{ $t('message.hello') }}</h1>
      <form class="p-2 space-y-5">
        <n-input
          type="text"
          size="large"
          :placeholder="`${'用户名'}/${'邮箱'}/${'手机号'}`"
        />
        <n-input
          type="password"
          size="large"
          :placeholder="'密码'"
          autocomplete
        />
        <n-button class="w-full h-10 bg-green-600 text-white">{{
          '登录'
        }}</n-button>
        <n-divider title-placement="center" class="text-sm">
          {{ '第三方登录' }}
        </n-divider>
        <div class="flex justify-center space-x-5">
          <n-icon size="32" class="text-green-500">
            <logo-wechat />
          </n-icon>
          <n-icon size="32">
            <logo-github />
          </n-icon>
        </div>
      </form>
    </div>
  </main>
</template>
