# 前端相关项目
> 注意事项
> 1. 项目使用 vue3(ecosystem) + typescript + vite4 + element-plus
> 2. packages 基于 TypeScript 开发
> 3. app/web 基于 javascript为主，TypeScript为辅开发(*.js 与 *.d.ts 搭配)
> 4. 项目使用 pnpm workspace 管理依赖，使用 pnpm install 安装依赖

## 项目结构

```js
├── app                         
│   ├── sdkjs                   // sdk on javascript
|   |   ├── src
|   |   ├── package.json
|   |   └── README.md
│   ├── web                     // web frontend
|   |   ├── src
|   |   ├── package.json
|   |   └── README.md
├── packages
│   ├── @cskefu                 // @cskefu library
│   │   ├── cli                 // cli tool
|   |   |   ├── package.json
|   |   |   └── README.md
│   │   ├── core                // core library
|   |   |   ├── package.json
│   │   |   └── README.md
│   ├── shared                  // shared library
│   │   ├── includes            // project config
|   |   |   ├── package.json
│   │   |   └── README.md
│   │   ├── utils               // utils library
|   |   |   ├── package.json
│   │   |   └── README.md
│   │   └── README.md
│   └── ui                      // design component
|       ├── package.json
│       └── README.md
├── pnpm-workspace.yaml         // pnpm workspace config
└── README.md
```

## 如何运行

| WORKSPACE| 目录 | 说明 |
| --- | --- | --- |
| @cskefu/web | app/web | 前端业务相关主题项目 |
| @cskefu/cli | packages/@cskefu/cli | cli 工具 |
| @cskefu/core | packages/@cskefu/core | 插件及核心系统引擎 |
| @cskefu/includes | packages/shared/includes | 共享项目基础配置 |
| @cskefu/utils | packages/shared/utils | 共享公用工具包 |
| @cskefu/ui | packages/ui | 基础组件及业务组件（基于 element-plus) |

```shell
pnpm install

pnpm --filter {workspace} run {script}
```