package sh.talonfox.enhancedweather.mixin;

import net.minecraft.entity.LightningEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.client.render.BackgroundRenderer;

@Mixin(LightningEntity.class)
public class MixinLightningEntity {
    /*@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"))
    public void overrideSound(World instance, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance) {
        if(sound.equals(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER)) {
            instance.playSound(x,y,z,sound,category,volume,1.0F,useDistance);
        } else {
            instance.playSound(x,y,z,sound,category,volume,pitch,useDistance);
        }
    }*/

    /**
     * @author TalonFox
     * @reason To prevent excessive amounts of fire spawning
     */
    @Overwrite
    private void spawnFire(int spreadAttempts) {
        // Do nothing...
    }
}
