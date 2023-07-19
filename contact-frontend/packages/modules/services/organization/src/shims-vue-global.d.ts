import { ComponentCustomProperties } from './shims-vue-global';

declare module "vue" {
  export interface ComponentCustomProperties {
    $t: (key: string) => string;
  }
}
