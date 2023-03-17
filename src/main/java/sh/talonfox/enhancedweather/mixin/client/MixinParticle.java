package sh.talonfox.enhancedweather.mixin.client;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfox.enhancedweather.EnhancedWeather;

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
        if(EnhancedWeather.CONFIG.Client_ParticleWind) {
            if (world.getDimensionKey().equals(DimensionTypes.OVERWORLD)) {
                if (collidesWithWorld) {
                    if(world.getTopPosition(Heightmap.Type.MOTION_BLOCKING,new BlockPos((int)prevPosX,(int)prevPosY,(int)prevPosZ)).getY() <= prevPosY) {
                        Vec3d result = EnhancedWeather.CLIENT_WIND.ApplyWindForce(new Vec3d(velocityX, velocityY, velocityZ), 1F, 1F / 20F, 0.5F);
                        velocityX = result.x;
                        velocityZ = result.z;
                    }
                }
            } else if (world.getDimensionKey().equals(DimensionTypes.THE_NETHER)) {
                if (collidesWithWorld) {
                    Vec3d result = EnhancedWeather.NETHER_CLIENT_WIND.ApplyWindForce(new Vec3d(velocityX, velocityY, velocityZ), 1F, 1F / 20F, 0.5F);
                    velocityX = result.x;
                    velocityZ = result.z;
                }
            }
        }
    }
}
