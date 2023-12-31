package sh.talonfox.enhancedweather.mixin.client;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfox.enhancedweather.CloudRenderManager;
import sh.talonfox.enhancedweather.EnhancedWeatherClient;
import sh.talonfox.enhancedweather.config.EnhancedWeatherConfig;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "renderWeather(Lnet/minecraft/client/render/LightmapTextureManager;FDDD)V", at = @At("HEAD"), cancellable = true)
    public void rain(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        if(EnhancedWeatherConfig.Client_ParticleRain) {
            ci.cancel();
        }
    }

    @Redirect(method = "tickRainSplashing(Lnet/minecraft/client/render/Camera;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"))
    public float rainSound(ClientWorld instance, float v) {
        return MathHelper.clamp(EnhancedWeatherClient.rain,0,1);
    }

    @Redirect(method = "tickRainSplashing(Lnet/minecraft/client/render/Camera;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    public void tickRain(ClientWorld instance, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }
    @Inject(method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FDDD)V", at = @At("HEAD"), cancellable = true)
    public void newCloudRender(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        CloudRenderManager.render(matrices,projectionMatrix,tickDelta,cameraX,cameraY,cameraZ);
        ci.cancel();
    }
}
