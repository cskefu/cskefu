import { locales } from '@cskefu/i18n';
import { ComponentCustomProperties } from './shims-vue-global.d';

declare module "vue" {
  export interface ComponentCustomProperties {
    $t: (key: string) => string;
    $i18n: { locale: string };
  }
}
