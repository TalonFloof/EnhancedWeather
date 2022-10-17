package sh.talonfox.enhancedweather.common.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import sh.talonfox.enhancedweather.common.ItemGroupRegister;

public class BlockRegister {
    public static final Block RADAR_TIER1_BLOCK = new RadarTier1Block(FabricBlockSettings.of(Material.METAL).strength(2.0f).sounds(BlockSoundGroup.NETHERITE));
    public static final Block RADAR_TIER2_BLOCK = new RadarTier2Block(FabricBlockSettings.of(Material.METAL).strength(2.0f).sounds(BlockSoundGroup.NETHERITE));
    public static final Block RADAR_TIER3_BLOCK = new RadarTier3Block(FabricBlockSettings.of(Material.METAL).strength(2.0f).sounds(BlockSoundGroup.NETHERITE));

    public static void Initialize() {
        Registry.register(Registry.BLOCK, new Identifier("enhancedweather","radar_tier1"), RADAR_TIER1_BLOCK);
        Registry.register(Registry.ITEM, new Identifier("enhancedweather","radar_tier1"), new BlockItem(RADAR_TIER1_BLOCK, new Item.Settings().group(ItemGroupRegister.ITEM_GROUP)));
        Registry.register(Registry.BLOCK, new Identifier("enhancedweather","radar_tier2"), RADAR_TIER2_BLOCK);
        Registry.register(Registry.ITEM, new Identifier("enhancedweather","radar_tier2"), new BlockItem(RADAR_TIER2_BLOCK, new Item.Settings().group(ItemGroupRegister.ITEM_GROUP)));
        Registry.register(Registry.BLOCK, new Identifier("enhancedweather","radar_tier3"), RADAR_TIER3_BLOCK);
        Registry.register(Registry.ITEM, new Identifier("enhancedweather","radar_tier3"), new BlockItem(RADAR_TIER3_BLOCK, new Item.Settings().group(ItemGroupRegister.ITEM_GROUP)));
    }
}
