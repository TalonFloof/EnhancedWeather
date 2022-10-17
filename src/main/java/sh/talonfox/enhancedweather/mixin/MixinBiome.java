package sh.talonfox.enhancedweather.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
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
    public boolean canSetSnow(WorldView worldView, BlockPos pos) {
        if (!worldView.isSkyVisible(pos)) {
            return false;
        } else if (worldView.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        } else {
            if(Enhancedweather.SERVER_WEATHER.getClosestCloud(new Vec3d(pos.getX(), 200, pos.getZ()), 384, true, false, false, false, 0) == null)
                return false;
            Biome biome = worldView.getBiome(pos).value();
            return biome.getPrecipitation() == Biome.Precipitation.SNOW;
        }
    }
}
