package sh.talonfox.enhancedweather.mixin;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sh.talonfox.enhancedweather.EnhancedWeather;
import sh.talonfox.enhancedweather.WindManager;
import sh.talonfox.enhancedweather.api.EnhancedWeatherAPI;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;isRaining()Z"))
    public boolean isRaining(ServerWorld instance) {
        return true;
    }
    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;isThundering()Z"))
    public boolean isThundering(ServerWorld instance) {
        return true;
    }
    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;hasRain(Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean canSpawnLightning(ServerWorld instance, BlockPos blockPos) {
        if(instance.getBiome(blockPos).value().getPrecipitation(blockPos) == Biome.Precipitation.RAIN && EnhancedWeatherAPI.isThundering(instance, WindManager.windSpeed,blockPos.getX(),blockPos.getY(),blockPos.getZ())) {
            return true;
        }
        return false;
    }
    @Redirect(method = "tickChunk", at = @At(value="INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"))
    private int overrideLightningSpawn(Random instance, int i) {
        return i == 100000 ? instance.nextInt(25000) : instance.nextInt(i);
    }
}
