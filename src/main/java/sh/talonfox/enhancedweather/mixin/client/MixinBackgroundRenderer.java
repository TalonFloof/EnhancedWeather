package sh.talonfox.enhancedweather.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfox.enhancedweather.Enhancedweather;
import sh.talonfox.enhancedweather.weather.ClientsideManager;

@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {
    @Inject(method = "applyFog", at = @At(value = "RETURN"))
    private static void addWeatherFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        if(Enhancedweather.CONFIG.Client_PrecipitationFog) {
            if (MinecraftClient.getInstance().world.getDimensionKey().equals(DimensionTypes.OVERWORLD)) {
                if (MinecraftClient.getInstance().gameRenderer.getCamera().getSubmersionType().equals(CameraSubmersionType.NONE)) {
                    assert MinecraftClient.getInstance().player != null;
                    double yPos = MinecraftClient.getInstance().player.getY();
                    Biome biome = MinecraftClient.getInstance().world.getBiome(MinecraftClient.getInstance().player.getBlockPos()).value();
                    if (biome.getPrecipitation().equals(Biome.Precipitation.RAIN) && ClientsideManager.PrecipitationRate > 0 && yPos < 200 && yPos > 0) {
                        RenderSystem.setShaderFogStart(0F);
                        RenderSystem.setShaderFogEnd(MathHelper.lerp(ClientsideManager.PrecipitationRate, viewDistance, 32F));
                    }
                }
            }
        }
    }
}
