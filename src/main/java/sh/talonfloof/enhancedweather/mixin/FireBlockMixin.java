package sh.talonfloof.enhancedweather.mixin;

import net.minecraft.block.FireBlock;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FireBlock.class)
public class FireBlockMixin {
    @Redirect(method = "scheduledTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/random/Random;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;isRaining()Z"))
    public boolean forceRain(ServerWorld instance) {
        return true;
    }
}
