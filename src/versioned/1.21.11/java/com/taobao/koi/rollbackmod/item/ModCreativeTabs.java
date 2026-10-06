package com.taobao.koi.rollbackmod.item;

import com.taobao.koi.rollbackmod.RollbackMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeTabs {
    public static final CreativeModeTab MAIN = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(RollbackMod.MOD_ID, "main"),
            FabricItemGroup.builder()
                    .title(Component.translatable("itemGroup.rollbackmod"))
                    .icon(() -> new ItemStack(ModItems.INHALER))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.INHALER);
                        output.accept(ModItems.COCOON_CORE);
                        output.accept(ModItems.MOLTING_CORE);
                        output.accept(ModItems.TOWER_CORE);
                    })
                    .build()
    );

    private ModCreativeTabs() {
    }

    public static void register() {
        // 静态字段初始化即完成注册
    }
}
