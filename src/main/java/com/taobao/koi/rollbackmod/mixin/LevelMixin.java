package com.taobao.koi.rollbackmod.mixin;

import com.taobao.koi.rollbackmod.rollback.BlockRollbackManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric 没有方块改动事件，用 Mixin 拦截改方块入口
 * （setBlock / destroyBlock / removeBlock），记录方块改动供回溯恢复。
 * 作物生长、树叶凋零等原版方块变化也会一并被追踪（比 Forge 事件更完整）。
 */
@Mixin(Level.class)
public abstract class LevelMixin {

    @Inject(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
            at = @At("HEAD")
    )
    private void rollbackmod$trackSetBlock(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ServerLevel level) {
            BlockRollbackManager.rememberBlockBeforeChange(level, pos, level.getBlockState(pos));
        }
    }

    @Inject(
            method = "destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;I)Z",
            at = @At("HEAD")
    )
    private void rollbackmod$trackDestroyBlock(BlockPos pos, boolean drop, Entity entity, int flags, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ServerLevel level) {
            BlockRollbackManager.rememberBlockBeforeChange(level, pos, level.getBlockState(pos));
        }
    }

    @Inject(
            method = "removeBlock(Lnet/minecraft/core/BlockPos;Z)Z",
            at = @At("HEAD")
    )
    private void rollbackmod$trackRemoveBlock(BlockPos pos, boolean move, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ServerLevel level) {
            BlockRollbackManager.rememberBlockBeforeChange(level, pos, level.getBlockState(pos));
        }
    }
}
