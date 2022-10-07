package sh.talonfox.enhancedweather.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

public class RadarBlockEntity extends BlockEntity {
    public ItemStack ModuleSlot1 = new ItemStack(Items.AIR);
    public ItemStack ModuleSlot2 = new ItemStack(Items.AIR);
    public ItemStack ModuleSlot3 = new ItemStack(Items.AIR);

    public RadarBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegister.RADAR_BLOCK_ENTITY, pos, state);
    }


}
