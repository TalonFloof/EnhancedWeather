package sh.talonfox.enhancedweather.mixin.server;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sh.talonfox.enhancedweather.Enhancedweather;

@Mixin(ServerWorld.class)
public class MixinServerWorld {
    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;isRaining()Z"))
    private boolean isRaining(ServerWorld instance) {
        return true;
    }
    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;isThundering()Z"))
    private boolean isThundering(ServerWorld instance) {
        return true;
    }
    @Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;hasRain(Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean canSpawnLightning(ServerWorld instance, BlockPos blockPos) {
        Biome biome = instance.getBiome(blockPos).value();
        return Enhancedweather.SERVER_WEATHER.getClosestCloud(new Vec3d(blockPos.getX(), 200, blockPos.getZ()), 384, false, true, false, false, -1) != null
                && biome.getPrecipitation() == Biome.Precipitation.RAIN && biome.doesNotSnow(blockPos);
    }
    @Redirect(method = "tickChunk", at = @At(value="INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"))
    private int overrideLightningSpawn(Random instance, int i) {
        return i == 100000 ? instance.nextInt(Enhancedweather.CONFIG.Weather_LightningStrikeBaseChance) : instance.nextInt(i);
    }
}
