# Rollback Mod（Fabric 1.21.1 移植版）

这是 Rollback Mod 向 **Fabric 1.21.1** 的移植：玩法与优化版完全一致
（存档覆盖回溯、轻量化药芯（化茧/蜕皮/高塔）、单格吸入器、滚动天数 HUD、
多人生命/背包统一、hardcore 式毁灭等）。实现差异见下。

## 与 Forge/NeoForge 版的差异

| 项 | Forge 1.20.1 / NeoForge 1.21.1 | Fabric 1.21.1 |
| --- | --- | --- |
| 伤害拦截 | LivingHurtEvent / LivingDamageEvent.Pre | Mixin `LivingEntity#hurt` |
| 死亡拦截 | LivingDeathEvent 取消 | `ServerPlayerEvents.ALLOW_DEATH` |
| 方块追踪 | BlockEvent 系列 | Mixin `Level#setBlock/destroyBlock/removeBlock`（覆盖面更全） |
| 网络 | SimpleChannel / payload | `CustomPacketPayload` + `PayloadTypeRegistry` |
| HUD 黑屏层 | RegisterGuiOverlaysEvent / RegisterGuiLayersEvent | `HudRenderCallback`（原版 HUD 之后触发，遮挡聊天栏） |
| 存档数据 | SavedData（Forge 工厂） | 原版 `SavedData.Factory` 三参构造器 |
| 配置 | Cloth Config 11.x / 15.x | Cloth Config 15.x + Mod Menu |
| 游戏模式锁定 | PlayerChangeGameModeEvent | 每 tick enforce |

## 构建

- 需要 JDK 21（Gradle 8.12 由 wrapper 自动下载）。
- `java -classpath gradle\wrapper\gradle-wrapper.jar org.gradle.wrapper.GradleWrapperMain build`
- 产物：`build/libs/rollbackmod-1.1.0-1.21.1-fabric.jar`（模组名-模组版本-游戏版本-加载器）。

致谢：作者 HanMoyun；朋友 @liyuu、@LAST-iMP。
