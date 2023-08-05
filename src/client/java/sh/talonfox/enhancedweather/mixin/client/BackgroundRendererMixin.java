package sh.talonfox.enhancedweather.mixin.client;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sh.talonfox.enhancedweather.EnhancedWeatherClient;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"))
    private static float fogDarken(ClientWorld world, float delta) {
        return EnhancedWeatherClient.cloud;
    }
}
