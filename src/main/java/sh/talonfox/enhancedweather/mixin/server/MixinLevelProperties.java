package sh.talonfox.enhancedweather.mixin.server;

import com.mojang.serialization.Lifecycle;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfox.enhancedweather.Enhancedweather;
import sh.talonfox.enhancedweather.weather.ClientsideManager;
import sh.talonfox.enhancedweather.weather.ServersideManager;

@Mixin(LevelProperties.class)
public class MixinLevelProperties {
    @Inject(method = {"<init>(Lnet/minecraft/world/level/LevelInfo;Lnet/minecraft/world/gen/GeneratorOptions;Lcom/mojang/serialization/Lifecycle;)V"}, at = {@At("TAIL")})
    public void randomWeather(LevelInfo levelInfo, GeneratorOptions generatorOptions, Lifecycle lifecycle, CallbackInfo ci) {
        if(!generatorOptions.isDebugWorld()) {
            ServersideManager.IsNewWorld = Enhancedweather.CONFIG.Weather_RandomConditionInNewWorld;
            ClientsideManager.PrecipitationRate = 0F;
            ClientsideManager.PrecipitationIntensity = 0;
        }
    }
}
