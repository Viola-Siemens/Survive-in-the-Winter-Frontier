# misc_twf_zombie_animals（僵尸动物模块）

模块化拆分 M1（见 `docs/MODULARIZATION.md` 5.1）。当前状态：**阶段 0 工程骨架**，业务代码未迁入。

- modid：`misc_twf_zombie_animals`；内容命名空间：`misc_twf`（决策 D6，注册 id 不做迁移）
- Java 根包：`com.hexagram2021.misc_twf_zombie_animals`（内部沿用 common/client/mixin/server 分层）
- 内容域：8 种僵尸动物实体与行为/渲染/音效、僵尸化/免疫机制属主（免疫存档与公开 API）、Hordes 豁免联动
- 兄弟模块依赖：无（可选被 M4 消费免疫 API，方向 M4 → M1）
- 外部依赖草案：hordes（强制）；zombie_extreme（可选，数据引用）；版本区间待依赖就绪后定稿

## 构建与运行

```powershell
gradlew :misc_twf_zombie_animals:build          # 编译并打包
gradlew :misc_twf_zombie_animals:runClient      # dev 客户端（需装齐草案依赖）
gradlew :misc_twf_zombie_animals:data           # 数据生成到 src/generated/resources
```

## 迁移说明

- 免疫存档与 Hordes 联动代码迁入本模块时，把存档文件名/NBT key 带模块标识（见文档 7.2）；
- 随迁内容打开 `misc_twf_zombie_animals.mixins.json`（当前为空、required=false）并补 AT 条目；
- 语言与 sounds.json 等聚合文件统一放根目录 `shared-resources/`，勿放本模块。
