package sh.talonfloof.enhancedweather.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfloof.enhancedweather.EnhancedWeather;
import sh.talonfloof.enhancedweather.EnhancedWeatherClient;

@Mixin(Particle.class)
public class MixinParticle {
    @Shadow
    @Final
    protected ClientWorld world;
    @Shadow
    protected boolean collidesWithWorld;
    @Shadow
    protected double prevPosX;
    @Shadow
    protected double prevPosY;
    @Shadow
    protected double prevPosZ;
    @Shadow
    protected double velocityX;
    @Shadow
    protected double velocityY;
    @Shadow
    protected double velocityZ;
    @Inject(at = @At("TAIL"), method = "tick")
    private void applyWind(CallbackInfo ci) {
        if(EnhancedWeather.CONFIG.Misc_DimensionWhitelist().contains(world.getDimensionKey().getValue().toString())) {
            if (collidesWithWorld) {
                if(world.getTopPosition(Heightmap.Type.MOTION_BLOCKING,new BlockPos((int)Math.floor(prevPosX),(int)Math.floor(prevPosY),(int)Math.floor(prevPosZ))).getY() <= prevPosY) {
                    velocityX = EnhancedWeatherClient.windX/4F;
                    velocityZ = EnhancedWeatherClient.windZ/4F;
                }
            }
        }
    }
}
