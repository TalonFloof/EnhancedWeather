package sh.talonfox.enhancedweather.common;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import sh.talonfox.enhancedweather.common.blocks.BlockRegister;

public class ItemGroupRegister {
    public static final Item ITEMGROUP_ICON = new Item(new FabricItemSettings());

    public static void Initialize() {
        Registry.register(Registries.ITEM, new Identifier("enhancedweather","itemgroup_icon"), ITEMGROUP_ICON);
    }

    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder(
            new Identifier("enhancedweather", "enhancedweather_itemgroup"))
            .icon(() -> new ItemStack(ITEMGROUP_ICON))
            .entries((enabledFeatures, entries, operatorEnabled) -> {
                entries.add(new ItemStack(BlockRegister.RADAR_TIER1_BLOCK));
                entries.add(new ItemStack(BlockRegister.RADAR_TIER2_BLOCK));
                entries.add(new ItemStack(BlockRegister.RADAR_TIER3_BLOCK));
            })
            .build();
}
