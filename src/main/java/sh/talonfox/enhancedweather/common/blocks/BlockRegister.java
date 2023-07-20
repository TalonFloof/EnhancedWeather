package sh.talonfox.enhancedweather.common.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BlockRegister {
    public static final Block RADAR_TIER1_BLOCK = new RadarTier1Block(FabricBlockSettings.create().requiresTool().strength(2.0f).sounds(BlockSoundGroup.NETHERITE));
    public static final Block RADAR_TIER2_BLOCK = new RadarTier2Block(FabricBlockSettings.create().requiresTool().strength(2.0f).sounds(BlockSoundGroup.NETHERITE));
    public static final Block RADAR_TIER3_BLOCK = new RadarTier3Block(FabricBlockSettings.create().requiresTool().strength(2.0f).sounds(BlockSoundGroup.NETHERITE));

    public static void Initialize() {
        Registry.register(Registries.BLOCK, new Identifier("enhancedweather","radar_tier1"), RADAR_TIER1_BLOCK);
        Registry.register(Registries.ITEM, new Identifier("enhancedweather","radar_tier1"), new BlockItem(RADAR_TIER1_BLOCK, new Item.Settings()));
        Registry.register(Registries.BLOCK, new Identifier("enhancedweather","radar_tier2"), RADAR_TIER2_BLOCK);
        Registry.register(Registries.ITEM, new Identifier("enhancedweather","radar_tier2"), new BlockItem(RADAR_TIER2_BLOCK, new Item.Settings()));
        Registry.register(Registries.BLOCK, new Identifier("enhancedweather","radar_tier3"), RADAR_TIER3_BLOCK);
        Registry.register(Registries.ITEM, new Identifier("enhancedweather","radar_tier3"), new BlockItem(RADAR_TIER3_BLOCK, new Item.Settings()));
    }
}
