package sh.talonfox.enhancedweather.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import sh.talonfox.enhancedweather.Enhancedweather;

@Mixin(Biome.class)
public class MixinBiome {
    /**
     * @author TalonFox
     * @reason To allow for the creation of localized weather
     */
    @Overwrite
    public boolean canSetSnow(WorldView world, BlockPos pos) {
        if (!((Biome)(Object)this).doesNotSnow(pos)) {
            if (pos.getY() >= world.getBottomY() && pos.getY() < world.getTopY() && world.getLightLevel(LightType.BLOCK, pos) < 10) {
                BlockState blockState = world.getBlockState(pos);
                if (blockState.isAir() && Blocks.SNOW.getDefaultState().canPlaceAt(world, pos)) {
                    if (Enhancedweather.SERVER_WEATHER.getClosestCloud(new Vec3d(pos.getX(), 200, pos.getZ()), 384, true, false, false, false, 0) == null)
                        return false;
                    return true;
                }
            }
        }
        return false;
    }
}
