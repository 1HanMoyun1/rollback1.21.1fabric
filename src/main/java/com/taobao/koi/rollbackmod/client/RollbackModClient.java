package com.taobao.koi.rollbackmod.client;

import com.taobao.koi.rollbackmod.network.ModNetworking;
import net.fabricmc.api.ClientModInitializer;

public class RollbackModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModNetworking.registerClient();
        ClientDayHud.register();
    }
}
