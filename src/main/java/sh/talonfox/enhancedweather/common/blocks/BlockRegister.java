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
    public static final Block RADAR_BLOCK = new RadarBlock(FabricBlockSettings.of(Material.METAL).strength(2.0f).sounds(BlockSoundGroup.NETHERITE));
    public static BlockEntityType<RadarBlockEntity> RADAR_BLOCK_ENTITY;

    public static void Initialize() {
        Registry.register(Registry.BLOCK, new Identifier("enhancedweather","radar"), RADAR_BLOCK);
        Registry.register(Registry.ITEM, new Identifier("enhancedweather","radar"), new BlockItem(RADAR_BLOCK, new Item.Settings().group(ItemGroupRegister.ITEM_GROUP)));
        RADAR_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("enhancedweather","radar"), FabricBlockEntityTypeBuilder.create(RadarBlockEntity::new, RADAR_BLOCK).build(null));
    }
}
