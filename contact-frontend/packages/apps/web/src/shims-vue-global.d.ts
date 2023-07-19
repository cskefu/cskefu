import { ComponentCustomProperties } from './shims-vue-global.d';

declare module "vue" {
  export interface ComponentCustomProperties {
    $t: (key: string) => string;
  }
}
