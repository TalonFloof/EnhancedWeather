package sh.talonfloof.enhancedweather.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sh.talonfloof.enhancedweather.EnhancedWeather;
import sh.talonfloof.enhancedweather.api.EnhancedWeatherAPI;

@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "hasRain(Lnet/minecraft/util/math/BlockPos;)Z", at = @At("RETURN"), cancellable = true)
    public void hasRain(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!EnhancedWeatherAPI.isRaining(((World)(Object)this),pos.getX() - MathHelper.floor(EnhancedWeather.cloudX), pos.getZ() - MathHelper.floor(EnhancedWeather.cloudZ))) {
            cir.setReturnValue(false);
            return;
        }
        if (!((World)(Object)this).isSkyVisible(pos)) {
            cir.setReturnValue(false);
            return;
        }
        if (((World)(Object)this).getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            cir.setReturnValue(false);
            return;
        }
        Biome biome = ((World)(Object)this).getBiome(pos).value();
        cir.setReturnValue(biome.getPrecipitation(pos) == Biome.Precipitation.RAIN);
    }
}
