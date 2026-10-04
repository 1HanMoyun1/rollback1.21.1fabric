package com.taobao.koi.rollbackmod.config;

import com.taobao.koi.rollbackmod.RollbackMod;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/** Mod Menu 集成：在模组菜单里提供 Cloth Config 设置界面。 */
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ClothConfigScreen.create(parent);
    }
}
