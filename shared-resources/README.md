# 共享聚合资源目录

本目录是「命名空间级单文件」聚合资源的**单一来源**（决策 D6 / 文档 `docs/MODULARIZATION.md` 4.2）。

由于 `assets/misc_twf/lang/*.json`、`assets/misc_twf/sounds.json` 这类文件无法跨 jar 拆分，
它们由本目录统一维护，并经每个模块的构建（`gradle/module-common.gradle`）原样打进各自的 jar：

- 各 jar 中这些文件内容完全一致，任一 jar 生效结果相同；
- 按路径拆分的资源（模型 / 贴图 / 方块状态 / 音效 ogg 等）**不允许**放这里，
  必须只存在于某一个模块自己的 `src/main/resources` 中，严禁同路径重复。

## 目录约定

```
shared-resources/
  assets/misc_twf/
    lang/        # 全部语言文件（en_us.json、zh_cn.json 等）
    sounds.json  # 音效定义
```

当前处于阶段 0（工程骨架），尚未迁入任何聚合文件；迁入首个模块内容时把文件放到上述位置即可。
