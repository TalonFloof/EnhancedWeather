package sh.talonfox.enhancedweather.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import sh.talonfox.enhancedweather.Enhancedweather;

@Mixin(World.class)
public class MixinWorldServer {
    /**
     * @author TalonFox
     * @reason To allow for the creation of localized weather
     */
    @Overwrite
    public boolean hasRain(BlockPos pos) {
        World world = Enhancedweather.SERVER_WEATHER.getWorld();
        if(world != null) {
            if (!world.isSkyVisible(pos)) {
                return false;
            } else if (world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
                return false;
            } else {
                if(Enhancedweather.SERVER_WEATHER.getClosestCloud(new Vec3d(pos.getX(), 200, pos.getZ()), 384, true, false, false, false, 0) == null)
                    return false;
                Biome biome = world.getBiome(pos).value();
                return biome.getPrecipitation() == Biome.Precipitation.RAIN && biome.doesNotSnow(pos);
            }
        }
        return false;
    }
}
