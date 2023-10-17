import { ComponentCustomProperties } from 'vue'

declare module "vue" {
  export interface ComponentCustomProperties {
    $t: (key: string) => string;
    $i18n: { locale: string };
  }
}
