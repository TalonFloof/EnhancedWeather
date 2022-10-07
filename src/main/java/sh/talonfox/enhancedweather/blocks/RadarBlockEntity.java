package sh.talonfox.enhancedweather.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class RadarBlockEntity extends BlockEntity {
    public RadarBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegister.RADAR_BLOCK_ENTITY, pos, state);
    }

}
