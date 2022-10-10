package sh.talonfox.enhancedweather.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import sh.talonfox.enhancedweather.Enhancedweather;
import sh.talonfox.enhancedweather.weather.ClientsideManager;

@Mixin(World.class)
public class MixinWorld {

    @Shadow
    @Final
    public boolean isClient;
    /**
     * @author TalonFox
     * @reason To allow for the creation of localized weather
     */
    @Overwrite
    public float getRainGradient(float gradient) {
        if(isClient && Enhancedweather.CLIENT_WEATHER != null) {
            if(MinecraftClient.getInstance().player != null) {
                if (MinecraftClient.getInstance().player.getY() < 200)
                    return ClientsideManager.PrecipitationRate;
            }
        }
        return 0.0F;
    }
    /**
     * @author TalonFox
     * @reason To allow for the creation of localized weather
     */
    @Overwrite
    public float getThunderGradient(float gradient) {
        if(isClient && Enhancedweather.CLIENT_WEATHER != null) {
            if(MinecraftClient.getInstance().player != null) {
                if (MinecraftClient.getInstance().player.getY() < 200)
                    return ClientsideManager.PrecipitationIntensity>0?ClientsideManager.PrecipitationRate:0.0F;
            }
        }
        return 0.0F;
    }
    /**
     * @author TalonFox
     * @reason To allow for the creation of localized weather
     */
    @Overwrite
    public boolean hasRain(BlockPos pos) {
        World world = isClient?MinecraftClient.getInstance().world:Enhancedweather.SERVER_WEATHER.getWorld();
        if(world != null) {
            if (!world.isSkyVisible(pos)) {
                return false;
            } else if (world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
                return false;
            } else {
                if(isClient) {
                    if(ClientsideManager.PrecipitationRate < 0.2F)
                        return false;
                } else {
                    if(Enhancedweather.SERVER_WEATHER.getClosestCloud(new Vec3d(pos.getX(), 200, pos.getZ()), 384, 0, true) == null)
                        return false;
                }
                Biome biome = world.getBiome(pos).value();
                return biome.getPrecipitation() == Biome.Precipitation.RAIN && biome.doesNotSnow(pos);
            }
        }
        return false;
    }
}
