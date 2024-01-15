package sh.talonfloof.enhancedweather.mixin.client;

import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sh.talonfloof.enhancedweather.EnhancedWeatherClient;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Redirect(method = "getSkyColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"))
    private float skyDarken(ClientWorld world, float delta) {
        return EnhancedWeatherClient.cloud;
    }

    @Redirect(method = "getSkyColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getThunderGradient(F)F"))
    private float skyThunder(ClientWorld world, float delta) {
        return EnhancedWeatherClient.rainDest == 1 ? EnhancedWeatherClient.rain : 0;
    }

    @Redirect(method = "getCloudsColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"))
    private float darkenCloud(ClientWorld instance, float v) {
        return EnhancedWeatherClient.cloud;
    }

    @Redirect(method = "getCloudsColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getThunderGradient(F)F"))
    private float rainCloud(ClientWorld instance, float v) {
        return EnhancedWeatherClient.rainDest == 1 ? EnhancedWeatherClient.rain : 0;
    }

    @Redirect(method = "getSkyBrightness",  at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"))
    private float ambientDarkness(ClientWorld instance, float v) {
        return EnhancedWeatherClient.cloud;
    }

    @Redirect(method = "getSkyBrightness",  at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getThunderGradient(F)F"))
    private float thunderDarkness(ClientWorld instance, float v) {
        return EnhancedWeatherClient.rainDest == 1 ? EnhancedWeatherClient.rain : 0;
    }
}
