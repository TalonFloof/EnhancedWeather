package sh.talonfox.enhancedweather.mixin.client;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sh.talonfox.enhancedweather.EnhancedWeatherClient;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Redirect(method = "getSkyColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"))
    private float skyDarken(ClientWorld world, float delta) {
        return EnhancedWeatherClient.cloud;
    }

    @Redirect(method = "getSkyBrightness",  at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"))
    private float ambientDarkness(ClientWorld instance, float v) {
        return EnhancedWeatherClient.cloud;
    }
}
