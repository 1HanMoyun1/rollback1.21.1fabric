package com.taobao.koi.rollbackmod.rollback;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * 1.21.11 起原版 {@link SavedDataType} 的构造签名为
 * {@code (String, Supplier<T>, Codec<T>, DataFixTypes)}，不再携带 registry 上下文，
 * 因此这里在首次使用时把 {@code HolderLookup.Provider} 闭包进 codec 并缓存该实例。
 */
public final class SavedDataAccess {
    private static SavedDataType<RollbackSavedData> cached;

    private SavedDataAccess() {
    }

    public static RollbackSavedData get(MinecraftServer server) {
        SavedDataType<RollbackSavedData> type = cached;
        if (type == null) {
            HolderLookup.Provider registries = server.registryAccess();
            type = new SavedDataType<>(
                    RollbackSavedData.DATA_NAME,
                    RollbackSavedData::new,
                    CompoundTag.CODEC.xmap(
                            tag -> RollbackSavedData.readTag(tag, registries),
                            RollbackSavedData::writeTag),
                    DataFixTypes.LEVEL);
            cached = type;
        }
        return server.overworld().getDataStorage().computeIfAbsent(type);
    }
}
