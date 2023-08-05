package sh.talonfox.enhancedweather.mixin.client;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sh.talonfox.enhancedweather.EnhancedWeatherClient;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Redirect(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"))
    public float sunRemoval(ClientWorld clientWorld, float delta) {
        return EnhancedWeatherClient.cloud;
    }

    @Redirect(method = "renderWeather(Lnet/minecraft/client/render/LightmapTextureManager;FDDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"))
    public float rain(ClientWorld instance, float v) {
        return EnhancedWeatherClient.rain;
    }

    @Redirect(method = "tickRainSplashing(Lnet/minecraft/client/render/Camera;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"))
    public float rainSploosh(ClientWorld instance, float v) {
        return EnhancedWeatherClient.rain;
    }
}
