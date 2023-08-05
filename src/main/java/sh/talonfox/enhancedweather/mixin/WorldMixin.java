package sh.talonfox.enhancedweather.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sh.talonfox.enhancedweather.EnhancedWeather;

@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "hasRain", at = @At("RETURN"), cancellable = true)
    public void hasRain(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (EnhancedWeather.getHumidity(((World)(Object)this),pos.getX(),pos.getZ()) >= 50F) {
            cir.setReturnValue(false);
        } else if (!((World)(Object)this).isSkyVisible(pos)) {
            cir.setReturnValue(false);
        } else if (((World)(Object)this).getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            cir.setReturnValue(false);
        } else {
            Biome biome = ((World)(Object)this).getBiome(pos).value();
            cir.setReturnValue(biome.getPrecipitation(pos) == Biome.Precipitation.RAIN);
        }
    }
}
