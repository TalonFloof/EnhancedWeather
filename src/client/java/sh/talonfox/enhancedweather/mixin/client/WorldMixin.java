package sh.talonfox.enhancedweather.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sh.talonfox.enhancedweather.EnhancedWeatherClient;

@Environment(EnvType.CLIENT)
@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "getRainGradient", at = @At("RETURN"), cancellable = true)
    private void getRain(float delta, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(EnhancedWeatherClient.rain);
    }

    @Inject(method = "getThunderGradient", at = @At("RETURN"), cancellable = true)
    private void getThunder(float delta, CallbackInfoReturnable<Float> cir) {
        if(EnhancedWeatherClient.rainDest == 1F) {
            cir.setReturnValue(EnhancedWeatherClient.rain);
        }
        cir.setReturnValue(0F);
    }
}
