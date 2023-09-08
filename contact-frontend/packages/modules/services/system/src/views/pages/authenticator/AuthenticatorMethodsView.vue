<script setup lang="ts">
import {
  NButton,
  NCard,
  NLayout,
  NGrid,
  NGridItem,
  NIcon,
  NDataTable,
} from 'naive-ui'
import {
  MailOutline,
  KeyOutline,
  PersonCircleOutline,
  LogoAlipay,
} from '@vicons/ionicons5'
import { ref } from 'vue'
import wechat from '@cskefu/assets/third-logo/wechat.svg'
import github from '@cskefu/assets/third-logo/github.svg'
import facebook from '@cskefu/assets/third-logo/facebook.svg'
import google from '@cskefu/assets/third-logo/google.svg'
import microsoft from '@cskefu/assets/third-logo/microsoft.svg'

const NativeProvider = [
  {
    provider: 'username',
    label: '账号/密码',
    icon: KeyOutline,
    disabled: false,
  },
  {
    provider: 'email',
    label: '电子邮件/密码',
    icon: MailOutline,
    disabled: true,
  },
  {
    provider: 'guest',
    label: '游客访问',
    icon: PersonCircleOutline,
    disabled: true,
  },
]

const DomesticProvider = [
  {
    provider: 'alipay',
    label: '微信',
    image: wechat,
    description: '允许用户使用支付宝扫码登录。',
    disabled: true,
  },
  {
    provider: 'wechat',
    label: '支付宝',
    icon: LogoAlipay,
    description: '允许用户使用微信扫码。',
    disabled: true,
  },
]

const OtherProvider = [
  {
    provider: 'google',
    label: 'Goole',
    image: google,
    description: '允许用户使用微信扫码。',
    disabled: true,
  },
  {
    provider: 'Facebook',
    label: 'facebook',
    image: facebook,
    description: '允许用户使用微信扫码。',
    disabled: true,
  },
  {
    provider: 'microsoft',
    label: 'Microsoft',
    image: microsoft,
    description: '允许用户使用微信扫码。',
    disabled: true,
  },
  {
    provider: 'github',
    label: 'Github',
    image: github,
    description: '允许用户使用微信扫码。',
    disabled: true,
  },
]

let isShowList = ref(true)

let data = ref([{ provider: '电子邮件/密码', status: '已启动' }])

function onClickProvider(provider: string): void {
  // todo
  console.log(provider)
}
</script>
<template>
  <n-layout content-style="padding: 24px;">
    <n-card v-show="!isShowList" title="添加新提供方">
      <template #header-extra>
        <n-button @click="isShowList = true"> 返回 </n-button>
      </template>
      <n-grid cols="1 650:4" class="divide-x">
        <n-grid-item :span="0.8" class="p-2">
          <div class="leading-8">原生提供方</div>
          <div class="flex flex-col space-y-2">
            <n-button
              v-for="(item, index) in NativeProvider"
              :key="index"
              class="w-full justify-start"
              :disabled="item.disabled"
              @click="onClickProvider(item.provider)"
            >
              <template #icon>
                <n-icon v-if="item.icon" size="20" :component="item.icon" />
              </template>
              {{ item.label }}
            </n-button>
          </div>
        </n-grid-item>
        <n-grid-item :span="0.8" class="p-2">
          <div class="leading-8">国内提供方</div>
          <div class="flex flex-col space-y-2">
            <n-button
              v-for="(item, index) in DomesticProvider"
              :key="index"
              :disabled="item.disabled"
              class="w-full justify-start"
              @click="onClickProvider(item.provider)"
            >
              <template #icon>
                <n-icon v-if="item.icon" size="20" :component="item.icon" />
                <img v-if="item.image" :src="item.image" />
              </template>
              {{ item.label }}
            </n-button>
          </div>
        </n-grid-item>
        <n-grid-item :span="2" class="p-2">
          <div class="leading-8">其他提供方</div>
          <div class="flex flex-row flex-wrap justify-between">
            <n-button
              v-for="(item, index) in OtherProvider"
              :key="index"
              :disabled="item.disabled"
              class="w-1/2 justify-start mb-2"
              @click="onClickProvider(item.provider)"
            >
              <template #icon>
                <img v-if="item.image" :src="item.image" width="24" />
              </template>
              {{ item.label }}
            </n-button>
          </div>
        </n-grid-item>
      </n-grid>
    </n-card>
    <n-card v-show="isShowList">
      <div class="flex flex-col">
        <div class="flex justify-end p-2 pr-0">
          <n-button @click="isShowList = false"> 添加新提供方 </n-button>
        </div>
        <n-data-table
          :columns="[
            { title: '提供方', key: 'provider' },
            { title: '状态', key: 'status' },
          ]"
          :data="data"
        />
      </div>
    </n-card>
  </n-layout>
</template>
