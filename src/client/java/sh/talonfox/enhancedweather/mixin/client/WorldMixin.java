package sh.talonfox.enhancedweather.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sh.talonfox.enhancedweather.EnhancedWeatherClient;

@Environment(EnvType.CLIENT)
@Mixin(World.class)
public class WorldMixin {
    @Inject(method="getRainGradient(F)F",at = @At("TAIL"), cancellable = true)
    public void getRainGradient(float delta, CallbackInfoReturnable<Float> cir) {
       cir.setReturnValue(EnhancedWeatherClient.rain);
    }

    @Inject(method="getThunderGradient(F)F",at = @At("TAIL"), cancellable = true)
    public void getThunderGradient(float delta, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(EnhancedWeatherClient.rainDest == 1F ? EnhancedWeatherClient.rain : 0F);
    }
}
