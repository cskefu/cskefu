# 春松客服 - 前端项目相关

## 项目结构

```
├── README.md
├── env
│   ├── .env.dev
│   ├── .env.mock
│   ├── .env.release
├── packages
│   ├── apps
│   │   ├── cskefu            # web 端
│   ├── assets                # 静态资源
│   ├── modules
│   │   ├── composables       # 全局 composables
│   │   ├── models            # 全局 models
│   │   ├── locales           # 本地化数据
│   │   ├── mocks             # mock 数据
│   │   ├── services
│   │   │   ├── dashboard     # 工作台相关
│   │   │   ├── chat          # 客服对话聊天相关
│   │   │   ├── worker-order  # 工单模块
│   │   │   ├── seats         # 坐席模块
│   │   │   ├── setting       # 组织设置相关
│   │   │   ├── system        # 系统设置模块
│   │   │   ├── auth          # 权限/登录相关
│   ├── plugins               # 插件 (开发使用的三方库，例如：ProseMirror)
│   │   ├── prose-mirror      # prose-mirror 插件
│   │   ├── directives        # 自定义指令
│   ├── widgets               # 小组件
│   │   ├── core
│   │   ├── sdk
│   ├── shared
│   │   ├── ui                # 全局 UI 组件
│   │   ├── utils             # 全局 utils
├── public
├── scripts
├── tools
│   │   ├── setup-naiveui
│   │   ├── setup-tailwind
```

## How to Develop

### 1. 安装依赖

```bash
pnpm install
```

### 2. 启动项目

```bash
pnpm run dev
```

## License

[春松许可证 v1.0](https://www.cskefu.com/2023/06/25/chunsong-public-license-1-0/)