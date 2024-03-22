package sh.talonfloof.enhancedweather.mixin.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfloof.enhancedweather.CloudRenderManager;
import sh.talonfloof.enhancedweather.EnhancedWeather;
import sh.talonfloof.enhancedweather.config.EnhancedWeatherConfig;
import sh.talonfloof.enhancedweather.EnhancedWeatherClient;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Unique
    private static VertexConsumerProvider.Immediate capturedImmediate;

    @Inject(method = "renderWeather(Lnet/minecraft/client/render/LightmapTextureManager;FDDD)V", at = @At("HEAD"), cancellable = true)
    public void rain(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        if (EnhancedWeather.CONFIG.Client_ParticleRain()) {
            ci.cancel();
        }
    }

    @Redirect(method = "tickRainSplashing(Lnet/minecraft/client/render/Camera;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"))
    public float rainSound(ClientWorld instance, float v) {
        return MathHelper.clamp(EnhancedWeatherClient.rain, 0, 1);
    }

    @Redirect(method = "tickRainSplashing(Lnet/minecraft/client/render/Camera;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    public void tickRain(ClientWorld instance, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    @Inject(method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FDDD)V", at = @At("HEAD"), cancellable = true)
    public void newCloudRender(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if(EnhancedWeather.CONFIG.Misc_DimensionWhitelist().contains(world.getDimensionKey().getValue().toString())) {
            CloudRenderManager.render(matrices, projectionMatrix, tickDelta, cameraX, cameraY, cameraZ);
            ci.cancel();
        }
    }

    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", slice = @Slice(
            from = @At(
                    ordinal = 0, value = "INVOKE",
                    target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"
            )
    ), at = @At(
            shift = At.Shift.AFTER,
            ordinal = 0,
            value = "INVOKE",
            target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionf;)V"
    ))
    public void renderRainbow(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci) {
        if(!EnhancedWeatherClient.showRainbow || !EnhancedWeather.CONFIG.Client_ShowRainbow())
            return;
        ClientWorld world = MinecraftClient.getInstance().world;
        if(!EnhancedWeather.CONFIG.Misc_DimensionWhitelist().contains(world.getDimensionKey().getValue().toString())) {
            return;
        }
        matrices.push();
        Tessellator t = Tessellator.getInstance();
        GlStateManager._blendFuncSeparate(770, 771, 1, 0);
        long time = world.getTimeOfDay() + 1000;
        int day = (int) (time / 24000L);
        Random rand = Random.create(day * 0xFF);
        float angle1 = rand.nextFloat() * 360F;
        float angle2 = rand.nextFloat() * 360F;
        float effCelAng1 = world.getSkyAngle(tickDelta);
        if (effCelAng1 >= 0.75F && effCelAng1 < 0.80F) {
            effCelAng1 = MathHelper.lerp((effCelAng1 - 0.75F) / 0.05F, 0F, 0.4F);
        } else if(effCelAng1 >= 0.20F && effCelAng1 < 0.25F) {
            effCelAng1 = MathHelper.lerp((effCelAng1 - 0.20F) / 0.05F, 0.4F, 0F);
        } else if(effCelAng1 >= 0.25F && effCelAng1 < 0.75F) {
            effCelAng1 = 0;
        } else {
            effCelAng1 = 0.4F;
        }
        float finalAlpha = EnhancedWeatherClient.showRainbow ? (effCelAng1*(Math.min(200,EnhancedWeatherClient.wetness)/200F)) : 0F;
        RenderSystem.setShaderColor(1F, 1F, 1F, finalAlpha - (world.getRainGradient(tickDelta)*effCelAng1));
        matrices.multiply(new Quaternionf().rotateY((float)Math.toRadians(angle1)));
        matrices.multiply(new Quaternionf().rotateZ((float)Math.toRadians(angle2)));
        Matrix4f mat = matrices.peek().getPositionMatrix();
        t.getBuffer().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        for (int i = 0; i < 90; i++) {
            int j = i;
            if (i % 2 == 0) {
                j--;
            }

            float ang = j * (360F / 90F);
            float xp = (float) Math.cos(ang * Math.PI / 180F) * 10;
            float zp = (float) Math.sin(ang * Math.PI / 180F) * 10;

            float ut = ang * (1F / 360F);
            if (i % 2 == 0) {
                t.getBuffer().vertex(mat, xp, 2F, zp).texture(ut, 1F).next();
                t.getBuffer().vertex(mat, xp, 0, zp).texture(ut, 0).next();
            } else {
                t.getBuffer().vertex(mat, xp, 0, zp).texture(ut, 0).next();
                t.getBuffer().vertex(mat, xp, 2F, zp).texture(ut, 1F).next();
            }

        }
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, new Identifier("enhancedweather","textures/sky/rainbow.png"));
        t.draw();
        matrices.pop();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F - EnhancedWeatherClient.cloud);
        GlStateManager._blendFuncSeparate(770, 1, 1, 0);
    }
}
