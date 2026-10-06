package com.taobao.koi.rollbackmod.item;

import com.taobao.koi.rollbackmod.RollbackMod;
import com.taobao.koi.rollbackmod.core.CoreType;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public final class ModItems {
    /** 吸入器：装入 1 枚药芯，长按右键（喝水动作）触发。**无限耐久**（无最大耐久，永不损坏）。 */
    public static final Item INHALER = register("inhaler",
            key -> new InhalerItem(new Item.Properties().setId(key).stacksTo(1).rarity(Rarity.UNCOMMON)));

    public static final Item COCOON_CORE = registerCore(CoreType.COCOON);
    public static final Item MOLTING_CORE = registerCore(CoreType.MOLTING);
    public static final Item TOWER_CORE = registerCore(CoreType.TOWER);

    private ModItems() {
    }

    public static void register() {
        // 静态字段初始化即完成注册
    }

    public static Optional<CoreType> getCoreType(ItemStack stack) {
        if (stack.getItem() instanceof CoreItem coreItem) {
            return Optional.of(coreItem.getCoreType());
        }
        return Optional.empty();
    }

    public static boolean isCore(ItemStack stack) {
        return getCoreType(stack).isPresent();
    }

    private static Item registerCore(CoreType type) {
        return register(type.registryName(),
                key -> new CoreItem(type, new Item.Properties().setId(key).stacksTo(1).rarity(Rarity.RARE)));
    }

    /** 1.21.2 起原版要求 {@code Item.Properties.setId(...)}，否则注册/使用时报 “Item id not set”。 */
    private static Item register(String name, Function<ResourceKey<Item>, Item> factory) {
        Identifier id = Identifier.fromNamespaceAndPath(RollbackMod.MOD_ID, name);
        return Registry.register(BuiltInRegistries.ITEM, id, factory.apply(ResourceKey.create(Registries.ITEM, id)));
    }
}
