<script setup lang="ts">
import {
  NButton,
  NCard,
  NLayout,
  NGrid,
  NGridItem,
  NIcon,
  FormInst,
  NForm,
  NFormItem,
  NSwitch,
  NDataTable,
  NInput,
} from 'naive-ui'
import {
  MailOutline,
  KeyOutline,
  LogoFacebook,
  LogoGithub,
  PersonCircleOutline,
  LogoAlipay,
} from '@vicons/ionicons5'
import { ref } from 'vue'
import wechat from '@cskefu/assets/third-logo/wechat.svg'
import google from '@cskefu/assets/third-logo/google.svg'
import microsoft from '@cskefu/assets/third-logo/microsoft.svg'

const formProvider = ref<FormInst | null>(null)
let modelProvider = ref({
  switchValue: false,
})
let modelProviderApp = ref({
  switchValue: false,
  id: '',
  password: '',
})
const rules = {
  id: {
    required: true,
    message: '请输入正确的ID',
    trigger: ['input'],
  },
  password: {
    required: true,
    message: '请输入正确的密钥',
    trigger: ['input'],
  },
}
let isShowFormProvider = ref('')
let isShowForm = ref(false)
let isShowList = ref(true)

const columns = ref([
  { title: '提供方', key: 'provider' },
  { title: '状态', key: 'status' },
])

let data = ref([{ provider: '电子邮件/密码', status: '已启动' }])

const handleValidateButtonClick = () => {
  formProvider.value?.validate((errors) => {
    if (!errors) {
      console.log('保存成功')
      isShowForm.value = false
      isShowList.value = true
    } else {
      console.log('保存失败')
    }
  })
  modelProvider.value = { switchValue: false }
  modelProviderApp.value = { switchValue: false, id: '', password: '' }
}

const handleCloseButtonClick = () => {
  isShowForm.value = false
  modelProvider.value = { switchValue: false }
  modelProviderApp.value = { switchValue: false, id: '', password: '' }
}

function onClickProvider(str: string): void {
  isShowForm.value = true
  isShowFormProvider.value = str
}
</script>
<template>
  <n-layout content-style="padding: 24px;">
    <n-card v-show="!isShowList">
      <div class="provider-form" v-show="isShowForm">
        <n-form v-if="isShowFormProvider == 'email'" class="provider-form-mail" ref="formProvider" :model="modelProvider"
          label-placement="left" label-align="left" label-width="160">
          <n-form-item label="电子邮件/密码" path="switchValue">
            <div class="provider-form-item">
              <n-switch v-model:value="modelProvider.switchValue"></n-switch>
            </div>
          </n-form-item>
          <n-form-item>
            <p>
              允许用户使用自己的电子邮件地址和密码进行注册。我们的 SDK
              还提供电子邮件地址验证、密码恢复和电子邮件地址更改等基本功能。
            </p>
          </n-form-item>
          <div class="provider-btn">
            <n-button secondary round @click="handleCloseButtonClick()">
              取消
            </n-button>
            <n-button secondary round type="primary" @click="handleValidateButtonClick()">
              保存
            </n-button>
          </div>
        </n-form>

        <n-form v-if="isShowFormProvider == 'password'" ref="formProvider" class="provider-form-password"
          :model="modelProvider" label-placement="left" label-align="left" label-width="80">
          <n-form-item label="账号/密码" path="switchValue">
            <div class="provider-form-item">
              <n-switch v-model:value="modelProvider.switchValue"></n-switch>
            </div>
          </n-form-item>
          <n-form-item>
            <p>
              允许用户使用自己的账号和密码进行注册。我们的 SDK
              还提供找回账号、密码重置和密码修改等基本功能。
            </p>
          </n-form-item>
          <div class="provider-btn">
            <n-button secondary round @click="handleCloseButtonClick()">
              取消
            </n-button>
            <n-button secondary round type="primary" @click="handleValidateButtonClick()">
              保存
            </n-button>
          </div>
        </n-form>
        <n-form v-if="isShowFormProvider == 'person'" class="provider-form-person" ref="formProvider"
          :model="modelProvider" label-placement="left" label-align="left" label-width="80">
          <n-form-item label="游客访问" path="switchValue">
            <div class="provider-form-item">
              <n-switch v-model:value="modelProvider.switchValue"></n-switch>
            </div>
          </n-form-item>
          <n-form-item>
            <p>
              在您的应用中启用匿名访客账号，这样可让您强制执行针对特定用户的安全性规则和春松客服规则，而不需要用户提供凭据。
            </p>
          </n-form-item>
          <div class="provider-btn">
            <n-button secondary round @click="handleCloseButtonClick()">
              取消
            </n-button>
            <n-button secondary round type="primary" @click="handleValidateButtonClick()">
              保存
            </n-button>
          </div>
        </n-form>
        <n-form v-if="isShowFormProvider == 'wechat'" class="provider-form-wechat" ref="formProvider"
          :model="modelProvider" label-placement="left" label-align="left" label-width="160">
          <n-form-item label="微信扫码登录" path="switchValue">
            <div class="provider-form-item">
              <n-switch v-model:value="modelProvider.switchValue"></n-switch>
            </div>
          </n-form-item>
          <n-form-item>
            <p>允许用户使用微信扫码。</p>
          </n-form-item>
          <div class="provider-btn">
            <n-button secondary round @click="handleCloseButtonClick()">
              取消
            </n-button>
            <n-button secondary round type="primary" @click="handleValidateButtonClick()">
              保存
            </n-button>
          </div>
        </n-form>
        <n-form v-if="isShowFormProvider == 'alipay'" class="provider-form-alipay" ref="formProvider"
          :model="modelProvider" label-placement="left" label-align="left" label-width="160">
          <n-form-item label="支付宝扫码" path="switchValue">
            <div class="provider-form-item">
              <n-switch v-model:value="modelProvider.switchValue"></n-switch>
            </div>
          </n-form-item>
          <n-form-item>
            <p>允许用户使用支付宝扫码登录。</p>
          </n-form-item>
          <div class="provider-btn">
            <n-button secondary round @click="handleCloseButtonClick()">
              取消
            </n-button>
            <n-button secondary round type="primary" @click="handleValidateButtonClick()">
              保存
            </n-button>
          </div>
        </n-form>
        <n-form v-if="isShowFormProvider == 'google'" class="provider-form-google" :rules="rules" ref="formProvider"
          :model="modelProviderApp" label-placement="left" label-align="left" label-width="160">
          <n-form-item label="Google" path="switchValue">
            <div class="provider-form-item">
              <n-switch v-model:value="modelProvider.switchValue"></n-switch>
            </div>
          </n-form-item>
          <n-form-item path="id" label="Web客户端ID">
            <n-input v-model:value="modelProviderApp.id" @keydown.enter.prevent />
          </n-form-item>
          <n-form-item path="password" label="Web客户端密钥">
            <n-input v-model:value="modelProviderApp.password" @keydown.enter.prevent />
          </n-form-item>
          <n-form-item>
            <p>
              重要提示：若要为您的 Android 应用启用 Google
              登录功能，您必须为每个应用提供
              <n-button text tag="a" href="https://developers.google.com/android/guides/client-auth?hl=zh-cn"
                target="_blank" type="primary">
                SHA-1 版本指纹
              </n-button>
            </p>
          </n-form-item>
          <div class="provider-btn">
            <n-button secondary round @click="handleCloseButtonClick()">
              取消
            </n-button>
            <n-button secondary round type="primary" @click="handleValidateButtonClick()">
              保存
            </n-button>
          </div>
        </n-form>
        <n-form v-if="isShowFormProvider == 'facebook'" class="provider-form-facebook" ref="formProvider"
          :model="modelProviderApp" :rules="rules" label-placement="left" label-align="left" label-width="80">
          <n-form-item label="Facebook" path="switchValue">
            <div class="provider-form-item">
              <n-switch v-model:value="modelProviderApp.switchValue"></n-switch>
            </div>
          </n-form-item>
          <n-form-item path="id" label="应用ID">
            <n-input v-model:value="modelProviderApp.id" @keydown.enter.prevent />
          </n-form-item>
          <n-form-item path="password" label="应用密钥">
            <n-input v-model:value="modelProviderApp.password" @keydown.enter.prevent />
          </n-form-item>
          <n-form-item>
            <p>
              若要完成设置，请在您的 Facebook 应用配置中添加此 OAuth 重定向
              URI。
              <n-button text tag="a"
                href="https://firebase.google.com/docs/auth/?hl=zh&authuser=0&_gl=1*yqljpk*_ga*MjEyMzg3MjAyNi4xNjkyNTM0MjU4*_ga_CW55HF8NVT*MTY5MzU1NzM3Ni4xMC4xLjE2OTM1NTc0MDYuMC4wLjA."
                target="_blank" type="primary">
                了解详情
              </n-button>
            </p>
          </n-form-item>
          <div class="provider-btn">
            <n-button secondary round @click="handleCloseButtonClick()">
              取消
            </n-button>
            <n-button secondary round type="primary" @click="handleValidateButtonClick()">
              保存
            </n-button>
          </div>
        </n-form>
        <n-form v-if="isShowFormProvider == 'microsoft'" class="provider-form-microsoft" ref="formProvider"
          :model="modelProviderApp" :rules="rules" label-placement="left" label-align="left" label-width="80">
          <n-form-item label="Microsoft" path="switchValue">
            <div class="provider-form-item">
              <n-switch v-model:value="modelProviderApp.switchValue"></n-switch>
            </div>
          </n-form-item>
          <n-form-item path="id" label="应用ID">
            <n-input v-model:value="modelProviderApp.id" @keydown.enter.prevent />
          </n-form-item>
          <n-form-item path="password" label="应用密钥">
            <n-input v-model:value="modelProviderApp.password" @keydown.enter.prevent />
          </n-form-item>
          <n-form-item>
            <p>
              若要完成设置，请在您的 Facebook 应用配置中添加此 OAuth 重定向
              URI。
              <n-button text tag="a"
                href="https://firebase.google.com/docs/auth/?hl=zh&authuser=0&_gl=1*r9xise*_ga*MjEyMzg3MjAyNi4xNjkyNTM0MjU4*_ga_CW55HF8NVT*MTY5MzU2MzM0My4xMS4wLjE2OTM1NjMzNTAuMC4wLjA."
                target="_blank" type="primary">
                了解详情
              </n-button>
            </p>
          </n-form-item>
          <div class="provider-btn">
            <n-button secondary round @click="handleCloseButtonClick()">
              取消
            </n-button>
            <n-button secondary round type="primary" @click="handleValidateButtonClick()">
              保存
            </n-button>
          </div>
        </n-form>
        <n-form v-if="isShowFormProvider == 'github'" class="provider-form-github" ref="formProvider"
          :model="modelProviderApp" :rules="rules" label-placement="left" label-align="left" label-width="80">
          <n-form-item label="GitHub" path="switchValue">
            <div class="provider-form-item">
              <n-switch v-model:value="modelProviderApp.switchValue"></n-switch>
            </div>
          </n-form-item>
          <n-form-item path="id" label="应用ID">
            <n-input v-model:value="modelProviderApp.id" @keydown.enter.prevent />
          </n-form-item>
          <n-form-item path="password" label="应用密钥">
            <n-input v-model:value="modelProviderApp.password" @keydown.enter.prevent />
          </n-form-item>
          <n-form-item>
            <p>
              若要完成设置，请在您的 Facebook 应用配置中添加此 OAuth 重定向
              URI。
              <n-button text tag="a"
                href="https://firebase.google.com/docs/auth/?hl=zh&authuser=0&_gl=1*r9xise*_ga*MjEyMzg3MjAyNi4xNjkyNTM0MjU4*_ga_CW55HF8NVT*MTY5MzU2MzM0My4xMS4wLjE2OTM1NjMzNTAuMC4wLjA."
                target="_blank" type="primary">
                了解详情
              </n-button>
            </p>
          </n-form-item>
          <div class="provider-btn">
            <n-button secondary round @click="handleCloseButtonClick()">
              取消
            </n-button>
            <n-button secondary round type="primary" @click="handleValidateButtonClick()">
              保存
            </n-button>
          </div>
        </n-form>
      </div>
      <div class="provider-headline" v-show="!isShowForm">添加登录提供方</div>
      <div v-show="!isShowForm">
        <n-grid cols="1 650:4">
          <n-grid-item>
            <div class="provider-col">
              <div class="provider-title">原生提供方</div>
              <ul class="provider-ul">
                <li @click="onClickProvider('email')">
                  <div class="provider-tile">
                    <div class="provider-name-group">
                      <n-icon class="provider-icon" size="20" :component="MailOutline" />
                      <div class="provider-name-text">电子邮件/密码</div>
                    </div>
                  </div>
                </li>
                <li @click="onClickProvider('password')">
                  <div class="provider-tile">
                    <div class="provider-name-group">
                      <n-icon class="provider-icon" size="20" :component="KeyOutline" />
                      <div class="provider-name-text">账号/密码</div>
                    </div>
                  </div>
                </li>
                <li @click="onClickProvider('person')">
                  <div class="provider-tile">
                    <div class="provider-name-group">
                      <n-icon class="provider-icon" size="20" :component="PersonCircleOutline" />
                      <div class="provider-name-text">游客访问</div>
                    </div>
                  </div>
                </li>
              </ul>
            </div>
          </n-grid-item>
          <n-grid-item>
            <div class="provider-col">
              <div class="provider-title">国内提供方</div>
              <ul class="provider-ul">
                <li @click="onClickProvider('wechat')">
                  <div class="provider-tile">
                    <div class="provider-name-group">
                      <!-- <n-icon class="provider-icon" size="20" :component="LogoWechat" /> -->
                      <div class="provider-icon-svg">
                        <img :src="wechat" alt="wechat" />
                      </div>
                      <div class="provider-name-text">微信</div>
                    </div>
                  </div>
                </li>
                <li @click="onClickProvider('alipay')">
                  <div class="provider-tile">
                    <div class="provider-name-group">
                      <n-icon class="provider-icon" size="20" :component="LogoAlipay" color="#1677ff" />
                      <div class="provider-name-text">支付宝</div>
                    </div>
                  </div>
                </li>
              </ul>
            </div>
          </n-grid-item>
          <n-grid-item :span="2">
            <div class="provider-col last">
              <div class="provider-title">其他提供方</div>
              <ul class="provider-ul other">
                <li @click="onClickProvider('google')">
                  <div class="provider-tile">
                    <div class="provider-name-group">
                      <!-- <n-icon class="provider-icon" size="20" :component="LogoGoogle" /> -->
                      <div class="provider-icon-svg provider-google">
                        <img :src="google" alt="google" />
                      </div>
                      <div class="provider-name-text">Google</div>
                    </div>
                  </div>
                </li>
                <li @click="onClickProvider('facebook')">
                  <div class="provider-tile">
                    <div class="provider-name-group">
                      <n-icon class="provider-icon" size="20" :component="LogoFacebook" color="#1977f3" />
                      <div class="provider-name-text">Facebook</div>
                    </div>
                  </div>
                </li>
                <li @click="onClickProvider('microsoft')">
                  <div class="provider-tile">
                    <div class="provider-name-group">
                      <!-- <n-icon class="provider-icon" size="20" :component="LogoMicrosoft" /> -->
                      <div class="provider-icon-svg provider-microsoft">
                        <img :src="microsoft" alt="Microsoft" />
                      </div>
                      <div class="provider-name-text">Microsoft</div>
                    </div>
                  </div>
                </li>
                <li @click="onClickProvider('github')">
                  <div class="provider-tile">
                    <div class="provider-name-group">
                      <n-icon class="provider-icon" size="20" :component="LogoGithub" />
                      <div class="provider-name-text">GitHub</div>
                    </div>
                  </div>
                </li>
              </ul>
            </div>
          </n-grid-item>
        </n-grid>
      </div>
    </n-card>
    <n-card v-show="isShowList">
      <div class="provider-list">
        <div class="btn">
          <n-button type="info" @click="isShowList = false">添加新提供方</n-button>
        </div>

        <n-data-table :columns="columns" :data="data" />
      </div>
    </n-card>
  </n-layout>
</template>

<style scoped>
.provider-tile {
  align-items: center;
  border-radius: 8px;
  box-sizing: border-box;
  display: flex;
  height: 52px;
  justify-content: space-between;
  max-height: 52px;
  min-width: 120px;
  padding: 4px 12px;
  width: 100%;
  border: 1px solid #041e491a;
  cursor: pointer;
}

.provider-tile:hover {
  background-color: #f5f5f5;
  border: 1px solid rgb(0 0 0 / 12%);
}

.provider-name-group {
  font-size: 14px;
  line-height: 20px;
  align-items: center;
  display: flex;
}

.provider-name-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.provider-ul li {
  padding: 6px;
  box-sizing: border-box;
  flex: 1 1 100%;
}

.provider-ul {
  align-items: center;
  display: flex;
  flex-flow: row wrap;
  list-style-type: none;
  justify-content: flex-start;
  margin: 0;
  padding: 0;
}

.provider-ul.other li {
  flex: 1 1 50%;
  max-width: none;
}

.provider-title {
  font-size: 14px;
  line-height: 20px;
  font-weight: 500;
  color: #000000de;
  padding: 12px 8px;
}

.provider-headline {
  font-size: 20px;
  line-height: 28px;
  font-weight: 400;
  color: #000000de;
  margin: 0 12px 32px;
}

.provider-col {
  border-right: 1px solid #0000001f;
  flex: 1 1 0;
  padding: 0 4px;
}

.provider-col.last {
  border-right: 0;
}

.provider-icon {
  margin-right: 10px;
}

.provider-form {
  margin: auto;
  margin-bottom: 32px;
  margin-top: 32px;
  max-width: 36em;
}

.provider-form-item {
  display: flex;
  justify-content: flex-end;
  width: 100%;
}

.provider-btn {
  display: flex;
  justify-content: flex-end;
}

.provider-btn .n-button {
  margin-left: 10px;
}

.provider-btn .n-button--default-type {
  background-color: rgba(46, 51, 56, 0.05);
}

.provider-btn .n-button--default-type:hover {
  background-color: rgba(46, 51, 56, 0.09);
}

.provider-btn .n-button--primary-type {
  background-color: rgba(24, 160, 88, 0.16);
}

.provider-btn .n-button--primary-type:hover {
  background-color: rgba(24, 160, 88, 0.22);
}

.provider-list .btn {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.provider-list .n-button--info-type {
  background-color: #2080f0;
}

.provider-list .n-button--info-type:hover {
  background-color: #4098fc;
}

.provider-icon-svg {
  height: 1em;
  width: 1.5em;
  line-height: 1em;
  text-align: center;
  display: inline-block;
  position: relative;
  fill: currentColor;
  transform: translateZ(0);
  margin-right: 10px;
}

.provider-microsoft {
  width: 1em;
}

.provider-google {
  height: 1.5em;
}

@media only screen and (max-width: 1100px) {
  .provider-col {
    border-right: 0;
  }
}
</style>
