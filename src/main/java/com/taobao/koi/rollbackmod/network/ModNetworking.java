package com.taobao.koi.rollbackmod.network;

import com.taobao.koi.rollbackmod.client.ClientDayHud;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class ModNetworking {
    private ModNetworking() {
    }

    /** 服务端入口：注册 payload 类型。 */
    public static void register() {
        PayloadTypeRegistry.playS2C().register(DayInfoPacket.TYPE, DayInfoPacket.STREAM_CODEC);
    }

    /** 客户端入口：注册 S2C 接收器。 */
    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(DayInfoPacket.TYPE, (packet, context) -> {
            context.client().execute(() -> ClientDayHud.update(packet));
        });
    }
}
