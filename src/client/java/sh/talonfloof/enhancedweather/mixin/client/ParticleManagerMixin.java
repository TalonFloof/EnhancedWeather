package sh.talonfloof.enhancedweather.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    @Inject(at = @At("HEAD"), method = "renderParticles(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/client/render/Camera;F)V")
    private void enableBackRender(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta, CallbackInfo ci) {
        RenderSystem.disableCull();
    }
    @Inject(at = @At("TAIL"), method = "renderParticles(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/client/render/Camera;F)V")
    private void disableBackRender(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta, CallbackInfo ci) {
        RenderSystem.enableCull();
    }
}
