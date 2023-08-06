package sh.talonfox.enhancedweather.mixin.client;

import net.minecraft.client.particle.CherryLeavesParticle;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import sh.talonfox.enhancedweather.EnhancedWeatherClient;

@Mixin(CherryLeavesParticle.class)
public abstract class CherryLeavesParticleMixin extends SpriteBillboardParticle {

    protected CherryLeavesParticleMixin(ClientWorld clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/CherryLeavesParticle;move(DDD)V"))
    public void moveWithWind(CallbackInfo ci) {
        ((CherryLeavesParticle)(Object)this).velocityX = EnhancedWeatherClient.windX/4F;
        ((CherryLeavesParticle)(Object)this).velocityZ = EnhancedWeatherClient.windZ/4F;
    }
}
