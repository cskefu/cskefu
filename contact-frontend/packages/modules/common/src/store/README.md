# @cskefu/common

> 封装了整个业务层的逻辑  (包括：数据获取、数据处理、数据存储等)  
> 为方便以后通过全局搜索框进行操作，使用 pinia，请确保界面操作命令实现的功能与命令行操作一致

## 目录结构

* module 为模块，一个模块对应一个 store，一个 store 对应一个模块
* action 为模块的 action，一个 action 对应一个执行命令指令 (通常以 ``do`` 开头)
* getter 为模块的 getter，一个 getter 对应一个获取命令指令 (通常以 ``get / is`` 开头)
