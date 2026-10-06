package com.taobao.koi.rollbackmod.mixin;

import com.taobao.koi.rollbackmod.event.CommonEvents;
import com.taobao.koi.rollbackmod.rollback.HiveManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric 没有伤害前事件，用 Mixin 拦截 {@link LivingEntity#hurt} 实现：
 * 多人共享伤害 → 致命回溯（对应 Forge/NeoForge 的 LivingDamageEvent.Pre 全流程）。
 * 返回 false 表示伤害被吸收。
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void rollbackmod$onHurt(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide() || !(self.level() instanceof ServerLevel)) {
            return;
        }

        if (!(self instanceof ServerPlayer player)) {
            return;
        }

        boolean consumed = false;
        if (amount > 0.0F) {
            consumed = HiveManager.shareDamage(player, source, amount);
        }
        if (!consumed) {
            consumed = CommonEvents.handleFatalRollback(player, amount);
        }
        if (consumed) {
            cir.setReturnValue(false);
        }
    }
}
