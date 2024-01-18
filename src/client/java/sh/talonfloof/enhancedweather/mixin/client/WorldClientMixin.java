package sh.talonfloof.enhancedweather.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
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
import sh.talonfloof.enhancedweather.EnhancedWeatherClient;
import sh.talonfloof.enhancedweather.api.EnhancedWeatherAPI;

@Environment(EnvType.CLIENT)
@Mixin(World.class)
public class WorldClientMixin {
    @Inject(method="getRainGradient(F)F",at = @At("TAIL"), cancellable = true)
    public void getRainGradient(float delta, CallbackInfoReturnable<Float> cir) {
        if(MinecraftClient.getInstance().player != null) {
            if (MinecraftClient.getInstance().player.getY() > MinecraftClient.getInstance().world.getDimensionEffects().getCloudsHeight())
                cir.setReturnValue(0F);
            else
                cir.setReturnValue(EnhancedWeatherClient.rain);
        }
    }

    @Inject(method="getThunderGradient(F)F",at = @At("TAIL"), cancellable = true)
    public void getThunderGradient(float delta, CallbackInfoReturnable<Float> cir) {
        if(MinecraftClient.getInstance().player != null) {
            if (MinecraftClient.getInstance().player.getY() > MinecraftClient.getInstance().world.getDimensionEffects().getCloudsHeight())
                cir.setReturnValue(0F);
            else
                cir.setReturnValue(EnhancedWeatherClient.rainDest == 1F ? EnhancedWeatherClient.rain : 0F);
        }
    }
    @Inject(method = "hasRain(Lnet/minecraft/util/math/BlockPos;)Z", at = @At("RETURN"), cancellable = true)
    public void hasRain(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!EnhancedWeatherAPI.isRainingClient(((World)(Object)this),pos.getX() - MathHelper.floor(EnhancedWeather.cloudX), pos.getZ() - MathHelper.floor(EnhancedWeather.cloudZ))) {
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
