package sh.talonfox.enhancedweather.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import sh.talonfox.enhancedweather.Enhancedweather;

@Mixin(World.class)
public class MixinWorld {
    @Shadow
    @Final
    public boolean isClient;
    /**
     * @author TalonFox
     * @reason To allow for the creation of localized weather
     */
    @Overwrite
    public float getRainGradient(float gradient) {
        if(isClient && Enhancedweather.CLIENT_WEATHER != null) {
            if(MinecraftClient.getInstance().player != null) {
                return 0F;
            } else {
                return 0.0F;
            }
        } else {
            return 0.0F;
        }
    }
}
