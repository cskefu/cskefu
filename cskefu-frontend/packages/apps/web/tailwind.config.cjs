/** @type {import('tailwindcss').Config} */
// eslint-disable-next-line no-undef
import base from '@cskefu/setup-tailwind'

export default {
  ...base,
  content: [
    './index.html',
    './src/**/*.{vue,js,ts,jsx,tsx}',
    './node_modules/@cskefu/**/*.{vue,js,ts,jsx,tsx}',
  ],
}
