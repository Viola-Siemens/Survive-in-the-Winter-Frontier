# misc_twf_wildlife（农牧生态模块）

模块化拆分 M2（见 `docs/MODULARIZATION.md` 5.2）。当前状态：**阶段 0 工程骨架**，业务代码未迁入。

- modid：`misc_twf_wildlife`；内容命名空间：`misc_twf`（决策 D6，注册 id 不做迁移）
- Java 根包：`com.hexagram2021.misc_twf_wildlife`（内部沿用 common/client/mixin/server 分层）
- 内容域：动物尸体、粪便肥料、产奶冷却、冬小麦与食物数据、随域微调（含 EasyDiet 盐分组）
- 兄弟模块依赖：无
- 外部依赖草案：无强制；可选 kubejs / cold_sweat / delightful / easydiet（代码依赖出现时再开）

## 构建与运行

```powershell
gradlew :misc_twf_wildlife:build                # 编译并打包
gradlew :misc_twf_wildlife:runClient            # dev 客户端
gradlew :misc_twf_wildlife:data                 # 数据生成到 src/generated/resources
```

## 迁移说明

- 尸体掉落中对第三方注册名的硬查必须改“可选模组判定 + 判空 + 原版替代”（见文档 6.2-E5）；
- 全实体注入的 `LivingEntityMixin` 保持零副作用，与其它模块共存时用 `misc_twf_wildlife$` 前缀 + priority 管理；
- 语言与 sounds.json 等聚合文件统一放根目录 `shared-resources/`，勿放本模块。
