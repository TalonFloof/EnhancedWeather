package sh.talonfox.enhancedweather.common;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import sh.talonfox.enhancedweather.common.blocks.BlockRegister;

public class ItemGroupRegister {
    public static final Item ITEMGROUP_ICON = new Item(new FabricItemSettings());

    public static void Initialize() {
        Registry.register(Registry.ITEM, new Identifier("enhancedweather","itemgroup_icon"), ITEMGROUP_ICON);
    }

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.create(
            new Identifier("enhancedweather", "enhancedweather_itemgroup"))
            .icon(() -> new ItemStack(ITEMGROUP_ICON))
            .appendItems(stacks -> {
                stacks.add(new ItemStack(BlockRegister.RADAR_TIER1_BLOCK));
                stacks.add(new ItemStack(BlockRegister.RADAR_TIER2_BLOCK));
                stacks.add(new ItemStack(BlockRegister.RADAR_TIER3_BLOCK));
            })
            .build();
}
