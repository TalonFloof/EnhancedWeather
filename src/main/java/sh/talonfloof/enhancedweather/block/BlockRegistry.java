package sh.talonfloof.enhancedweather.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class BlockRegistry {
    public static final Block RADAR_BLOCK = new RadarBlock(FabricBlockSettings.create().requiresTool().strength(2.0f).sounds(BlockSoundGroup.NETHERITE));
    public static final BlockItem RADAR_BLOCK_ITEM = new BlockItem(RADAR_BLOCK, new Item.Settings());
    public static void register() {
        Registry.register(Registries.BLOCK, new Identifier("enhancedweather","radar"), RADAR_BLOCK);
        Registry.register(Registries.ITEM, new Identifier("enhancedweather","radar"), RADAR_BLOCK_ITEM);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
            content.add(RADAR_BLOCK_ITEM);
        });
    }
}
