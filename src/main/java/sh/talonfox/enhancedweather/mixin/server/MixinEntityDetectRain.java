package sh.talonfox.enhancedweather.mixin.server;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sh.talonfox.enhancedweather.EnhancedWeather;

@Mixin(targets = {"net.minecraft.entity.passive.BeeEntity"})
public class MixinEntityDetectRain {
    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isRaining()Z"))
    public boolean localizeIsRaining(World world) {
        var pos = ((Entity)(Object)this).getBlockPos();
        Biome biome = world.getBiome(pos).value();
        return EnhancedWeather.SERVER_WEATHER.getClosestCloud(new Vec3d(pos.getX(), 200, pos.getZ()), 384, true, false, false, false, -1) != null
                && biome.getPrecipitation(pos) == Biome.Precipitation.RAIN && biome.doesNotSnow(pos);
    }
}
