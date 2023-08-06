package sh.talonfox.enhancedweather.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sh.talonfox.enhancedweather.EnhancedWeatherClient;

@Environment(EnvType.CLIENT)
@Mixin(World.class)
public class WorldMixin {
    /**
     * @author TalonFox
     * @reason For shader compatibility
     */
    @Overwrite
    public float getRainGradient(float delta) {
        return EnhancedWeatherClient.rain;
    }

    /**
     * @author TalonFox
     * @reason For shader compatibility
     */
    @Overwrite
    public float getThunderGradient(float delta) {
        return EnhancedWeatherClient.rainDest == 1F ? EnhancedWeatherClient.rain : 0F;
    }
}
