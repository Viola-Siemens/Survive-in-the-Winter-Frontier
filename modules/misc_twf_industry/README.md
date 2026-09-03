# misc_twf_industry（工业生态模块）

模块化拆分 M3（见 `docs/MODULARIZATION.md` 5.3）。当前状态：**阶段 0 工程骨架**，业务代码未迁入。

- modid：`misc_twf_industry`；内容命名空间：`misc_twf`（决策 D6，注册 id 不做迁移）
- Java 根包：`com.hexagram2021.misc_twf_industry`（内部沿用 common/client/mixin/server 分层）
- 内容域：能源装备与紫外线灯（D1）、回收炉、弹药模具工业
- 兄弟模块依赖：无
- 外部依赖草案：create / createaddition / curios / tetrachordlib / 枪械模组（superb-warfare，代码命名空间 tacz）；Registrate（compileOnly，runtime 由 create 提供）；ponder / JEI（可选）

## 构建与运行

```powershell
gradlew :misc_twf_industry:build                # 编译并打包
gradlew :misc_twf_industry:runClient            # dev 客户端（需装齐草案依赖）
gradlew :misc_twf_industry:data                 # 数据生成到 src/generated/resources
```

## 迁移说明

- 灯坐标存档独立为 `MISCTWFLampSavedData`（与 M1 免疫存档分离，见文档 7.2）；
- 回收炉配方书分类静态 import `com.tacz.guns.*`（E2）：迁入时保持枪械模组为编译+运行依赖，或先重构为可选注册；
- 语言与 sounds.json 等聚合文件统一放根目录 `shared-resources/`，勿放本模块。
