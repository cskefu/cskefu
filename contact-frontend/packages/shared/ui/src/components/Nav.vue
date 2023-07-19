<script setup lang="ts">
import logoUrl from '@cskefu/assets/img/logo.png'
import { NPopselect, NAvatar } from 'naive-ui'
import { ref } from 'vue'

const dropdownFlag = ref<boolean>(false)

defineProps({
  current: {
    type: String,
    default: '',
  },
  logoUrl: {
    type: String,
    default: logoUrl,
  },
  avatarUrl: {
    type: String,
    default: '',
  },
  navigations: {
    type: Array<any>,
    default: [],
  },
  notifications: {
    type: Array<any>,
    default: [],
  },
  dropdownMenus: {
    type: Array<any>,
    default: [],
  },
})

defineEmits(['update:current'])
</script>
<template>
  <nav class="bg-white border border-b-1 border-b-gray-300">
    <div class="px-4 sm:px-6 lg:px-8">
      <div class="flex h-16 items-center justify-between">
        <div class="flex items-center">
          <div class="flex-shrink-0 pt-2">
            <img class="h-10 w-18" :src="logoUrl" alt="Your Company" />
          </div>
        </div>
        <div class="hidden md:block">
          <div class="ml-10 flex items-baseline space-x-4">
            <a
              v-for="(nav, index) in navigations"
              :key="index"
              :class="`hover:bg-gray-300 hover:text-gray-900 rounded-md px-3 py-1 text-sm font-medium cursor-pointer ${
                current === nav.value
                  ? 'bg-gray-300 text-gray-900'
                  : 'text-gray-900'
              }`"
              @click="() => $emit('update:current', nav.value)"
            >
              {{ nav.label }}
            </a>
          </div>
        </div>
        <div class="hidden md:block">
          <div class="flex items-center space-x-4">
            <slot></slot>
            <!-- Profile dropdown -->
            <div class="relative ml-3">
              <n-popselect
                :options="dropdownMenus"
                class="w-60"
                trigger="click"
                size="large"
                :on-update-value="(value) => $emit('update:current', value)"
              >
                <button
                  type="button"
                  class="flex max-w-xs items-center rounded-full bg-gray-800 text-sm focus:outline-none"
                  aria-expanded="false"
                  aria-haspopup="true"
                >
                  <span class="sr-only">Open user menu</span>
                  <n-avatar round size="small" :src="avatarUrl" />
                </button>
                <template #action>
                  <slot name="dropMenuAppend"></slot>
                </template>
              </n-popselect>
            </div>
          </div>
        </div>
        <div class="-mr-2 flex md:hidden">
          <!-- Mobile menu button -->
          <button
            type="button"
            class="inline-flex items-center justify-center rounded-md p-2 text-gray-900 focus:ring-white"
            aria-controls="mobile-menu"
            aria-expanded="false"
            @click="() => (dropdownFlag = !dropdownFlag)"
          >
            <span class="sr-only">Open main menu</span>
            <!-- Menu open: "hidden", Menu closed: "block" -->
            <svg
              :class="`${dropdownFlag ? 'hidden' : ''} h-6 w-6`"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="1.5"
              stroke="currentColor"
              aria-hidden="true"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5"
              />
            </svg>
            <!-- Menu open: "block", Menu closed: "hidden" -->
            <svg
              :class="`${!dropdownFlag ? 'hidden' : ''} h-6 w-6`"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="1.5"
              stroke="currentColor"
              aria-hidden="true"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        </div>
      </div>
    </div>
    <!-- Mobile menu, show/hide based on menu state. -->
    <div v-show="dropdownFlag" class="md:hidden">
      <div class="space-y-1 px-2 pb-3 pt-2 sm:px-3">
        <a
          v-for="(nav, index) in navigations"
          :key="index"
          :class="`hover:bg-gray-300 hover:text-gray-900 block rounded-md px-3 py-2 text-base font-medium ${
            current === nav.value
              ? 'bg-gray-300 text-gray-900'
              : 'text-gray-900'
          }`"
          @click="() => $emit('update:current', nav.value)"
        >
          {{ nav.label }}
        </a>
      </div>
    </div>
  </nav>
</template>
