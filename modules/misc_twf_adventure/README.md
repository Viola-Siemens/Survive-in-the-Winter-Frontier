# misc_twf_adventure（冒险与探索模块）

模块化拆分 M4（见 `docs/MODULARIZATION.md` 5.4）。当前状态：**阶段 0 工程骨架**，业务代码未迁入。

- modid：`misc_twf_adventure`；内容命名空间：`misc_twf`（决策 D6，注册 id 不做迁移）
- Java 根包：`com.hexagram2021.misc_twf_adventure`（内部沿用 common/client/mixin/server 分层）
- 内容域：生化医疗玩法、旅行背包×枪械联动、深渊巢穴与怪物蛋世界生成、末世装饰（D2）
- 兄弟模块依赖：仅消费 M1 免疫 API（optional，方向 M4 → M1）
- 外部依赖草案：travelersbackpack / 枪械模组（superb-warfare）/ geckolib / expandability（强制）；zombie_extreme / kubejs（可选）；Sona（无 1.21.1，约定 API + TODO 占位）

## 构建与运行

```powershell
gradlew :misc_twf_adventure:build               # 编译并打包
gradlew :misc_twf_adventure:runClient           # dev 客户端（需装齐草案依赖）
gradlew :misc_twf_adventure:data                # 数据生成到 src/generated/resources
```

## 迁移说明

- 疫苗写入免疫经 M1 公开 API + 可选模组判定（M1 缺失时跳过，见文档 6.2-E7）；
- 结构调色板对 createdeco/verdure 等保持 DeferredBlock 懒解析（E6）；
- 巢穴战利品对 M3 材料 / tacz / zombie_extreme / kubejs 的引用按文档 6.2-E1/E3/E4 处置；
- 语言与 sounds.json 等聚合文件统一放根目录 `shared-resources/`，勿放本模块。
