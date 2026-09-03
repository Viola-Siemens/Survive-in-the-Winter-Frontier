# Survive in the Winter Frontier (misc_twf)

A Minecraft Mod for modpack "The Winter Frontier"

## 项目结构（模块化拆分进行中）

> 拆分方案与决策见 `docs/MODULARIZATION.md`（四模块终版）。当前处于阶段 0：工程骨架，业务代码尚未迁移。

- 根工程：过渡期单体（modid = `misc_twf`），业务代码仍在 `src/main`，迁移完成后退役。
- `modules/misc_twf_zombie_animals`：僵尸动物模块（骨架）
- `modules/misc_twf_wildlife`：农牧生态模块（骨架）
- `modules/misc_twf_industry`：工业生态模块（骨架）
- `modules/misc_twf_adventure`：冒险与探索模块（骨架）
- `shared-resources`：命名空间级聚合资源（lang/sounds.json）单一来源，各模块构建时冗余打包
- `gradle/module-common.gradle`：模块公共构建约定（4 个子工程共用）

模块内容域、依赖草案、迁移路线与验收标准详见 `docs/MODULARIZATION.md`；每个模块目录内另有 README。
