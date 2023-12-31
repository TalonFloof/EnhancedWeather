package sh.talonfox.enhancedweather.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfox.enhancedweather.EnhancedWeatherClient;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    /*@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"))
    private static float fogDarken(ClientWorld world, float delta) {
        return EnhancedWeatherClient.cloud;
    }*/

    @Inject(method = "applyFog", at = @At(value = "RETURN"))
    private static void addWeatherFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        if (MinecraftClient.getInstance().world.getDimensionKey().equals(DimensionTypes.OVERWORLD)) {
            if (MinecraftClient.getInstance().gameRenderer.getCamera().getSubmersionType().equals(CameraSubmersionType.NONE)) {

            }
        }
    }
}
