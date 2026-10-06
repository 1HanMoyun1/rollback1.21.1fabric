package com.taobao.koi.rollbackmod.config;

import com.taobao.koi.rollbackmod.RollbackMod;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

/** Mod Menu 集成：在模组菜单里提供 Cloth Config 设置界面（未安装 cloth-config 时不提供）。 */
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (!FabricLoader.getInstance().isModLoaded("cloth-config")) {
            return parent -> null;
        }
        return parent -> ClothConfigScreen.create(parent);
    }
}
