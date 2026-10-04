package com.taobao.koi.rollbackmod;

import com.taobao.koi.rollbackmod.config.RollbackConfig;
import com.taobao.koi.rollbackmod.core.CoreEffectRegistry;
import com.taobao.koi.rollbackmod.event.CommonEvents;
import com.taobao.koi.rollbackmod.item.ModCreativeTabs;
import com.taobao.koi.rollbackmod.item.ModItems;
import com.taobao.koi.rollbackmod.network.ModNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class RollbackMod implements ModInitializer {
    public static final String MOD_ID = "rollbackmod";

    @Override
    public void onInitialize() {
        RollbackConfig.load(FabricLoader.getInstance().getConfigDir().resolve("rollbackmod.json"));

        ModItems.register();
        ModCreativeTabs.register();

        CoreEffectRegistry.bootstrap();
        ModNetworking.register();
        CommonEvents.register();
    }
}
