package com.taobao.koi.rollbackmod.item;

import com.taobao.koi.rollbackmod.RollbackMod;
import com.taobao.koi.rollbackmod.core.CoreType;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public final class ModItems {
    /** 吸入器：装入 1 枚药芯，长按右键（喝水动作）触发。**无限耐久**（无最大耐久，永不损坏）。 */
    public static final Item INHALER = register("inhaler",
            new InhalerItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

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
        return register(type.registryName(), new CoreItem(type, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    }

    private static Item register(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(RollbackMod.MOD_ID, name), item);
    }
}
