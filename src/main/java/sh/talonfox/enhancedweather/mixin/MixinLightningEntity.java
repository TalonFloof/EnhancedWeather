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
    /**
     * @author TalonFox
     * @reason To prevent excessive amounts of fire spawning
     */
    @Overwrite
    private void spawnFire(int spreadAttempts) {
        // Do nothing...
    }
}
