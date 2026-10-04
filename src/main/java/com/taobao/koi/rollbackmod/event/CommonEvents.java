package com.taobao.koi.rollbackmod.event;

import com.taobao.koi.rollbackmod.config.RollbackConfig;
import com.taobao.koi.rollbackmod.rollback.ChunkTracker;
import com.taobao.koi.rollbackmod.rollback.DayCounterManager;
import com.taobao.koi.rollbackmod.rollback.HiveManager;
import com.taobao.koi.rollbackmod.rollback.RollbackManager;
import com.taobao.koi.rollbackmod.rollback.RollbackRestoreQueue;
import com.taobao.koi.rollbackmod.rollback.SelfDestructManager;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public final class CommonEvents {
    private static final Set<UUID> SKIP_NEXT_DEATH_ROLLBACK = new HashSet<>();

    private CommonEvents() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(CommonEvents::onServerTick);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> onPlayerJoin(handler.player));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> SelfDestructManager.enforce(newPlayer));
        ServerPlayerEvents.ALLOW_DEATH.register(CommonEvents::onAllowDeath);
        ServerChunkEvents.CHUNK_LOAD.register((level, chunk) -> ChunkTracker.onChunkLoad(level, chunk.getPos()));
        ServerChunkEvents.CHUNK_UNLOAD.register((level, chunk) -> ChunkTracker.onChunkUnload(level, chunk.getPos()));
        // 世界创建/首次加载时只存档一次，之后不再自动存档，存档全靠药芯
        ServerLifecycleEvents.SERVER_STARTED.register(server -> RollbackManager.ensureInitialCheckpoint(server));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> ChunkTracker.clearAll());
    }

    private static void onPlayerJoin(ServerPlayer player) {
        // 世界创建时的存档点里没有玩家：玩家加入时补拍快照进存档点，
        // 保证死亡回溯能把玩家送回加入时的状态（而不是原地不动）
        RollbackManager.addPlayerToCheckpoint(player.getServer(), player);
        SelfDestructManager.enforce(player);
        DayCounterManager.syncTo(player);
    }

    /** 死亡前的最后拦截：执行回溯并取消死亡（等价于取消 LivingDeathEvent）。 */
    private static boolean onAllowDeath(ServerPlayer player, DamageSource source, float amount) {
        if (RollbackManager.isRollingBack()) {
            return true;
        }
        if (consumeSkipNextDeathRollback(player)) {
            return true;
        }
        if (!RollbackConfig.enableDeathRollback) {
            return true;
        }
        if (!RollbackManager.hasCheckpoint(player.getServer())) {
            return true;
        }
        player.setHealth(Math.max(1.0F, player.getHealth()));
        RollbackManager.rollback(player.getServer(), "player_death");
        return false;
    }

    /** 致命伤害回溯（在 LivingEntityMixin 中调用）。返回 true 表示伤害已被吸收。 */
    public static boolean handleFatalRollback(ServerPlayer player, float amount) {
        if (RollbackManager.isRollingBack()) {
            return false;
        }
        if (hasSkipNextDeathRollback(player)) {
            return false;
        }
        if (!RollbackConfig.enableDeathRollback) {
            return false;
        }
        if (!RollbackManager.hasCheckpoint(player.getServer())) {
            return false;
        }
        if (amount < player.getHealth() + player.getAbsorptionAmount()) {
            return false;
        }

        player.setHealth(Math.max(1.0F, player.getHealth()));
        RollbackManager.rollback(player.getServer(), "fatal_damage");
        return true;
    }

    private static void onServerTick(MinecraftServer server) {
        DayCounterManager.tick(server);
        HiveManager.tick(server);
        RollbackRestoreQueue.tick();
        RollbackManager.clearRollbackJustHappened();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            SelfDestructManager.enforce(player);
        }
    }

    public static void markSkipNextDeathRollback(ServerPlayer player) {
        SKIP_NEXT_DEATH_ROLLBACK.add(player.getUUID());
    }

    public static boolean consumeSkipNextDeathRollback(ServerPlayer player) {
        return SKIP_NEXT_DEATH_ROLLBACK.remove(player.getUUID());
    }

    public static boolean hasSkipNextDeathRollback(ServerPlayer player) {
        return SKIP_NEXT_DEATH_ROLLBACK.contains(player.getUUID());
    }
}
