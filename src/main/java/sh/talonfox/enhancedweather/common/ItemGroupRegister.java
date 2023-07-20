package sh.talonfox.enhancedweather.common;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import sh.talonfox.enhancedweather.common.blocks.BlockRegister;

public class ItemGroupRegister {
    public static final Item ITEMGROUP_ICON = new Item(new FabricItemSettings());

    public static void Initialize() {
        Registry.register(Registries.ITEM, new Identifier("enhancedweather","itemgroup_icon"), ITEMGROUP_ICON);
        Registry.register(Registries.ITEM_GROUP, new Identifier("enhancedweather", "enhancedweather_itemgroup"),
                FabricItemGroup.builder()
                        .displayName(Text.literal("Enhanced Weather"))
                        .icon(() -> new ItemStack(ITEMGROUP_ICON))
                        .entries((context, entries) -> {
                            entries.add(new ItemStack(BlockRegister.RADAR_TIER1_BLOCK));
                            entries.add(new ItemStack(BlockRegister.RADAR_TIER2_BLOCK));
                            entries.add(new ItemStack(BlockRegister.RADAR_TIER3_BLOCK));
                        })
                        .build());
    }
}
