package sh.talonfox.enhancedweather.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import sh.talonfox.enhancedweather.EnhancedWeatherClient;

public class SnowParticle extends SpriteBillboardParticle {
    protected SnowParticle(ClientWorld clientWorld, double x, double y, double z, double r, double g, double b, SpriteProvider provider) {
        super(clientWorld, x, y, z, r, g, b);
        this.velocityX = 0.0D;
        this.velocityY = -1.0D;
        this.velocityZ = 0.0D;
        this.gravityStrength = 1.0F;
        this.scale = 0.25F;
        this.maxAge = 200;
        this.setSprite(provider);
    }

    public void tick() {
        this.velocityX = EnhancedWeatherClient.windX/2F;
        this.velocityZ = EnhancedWeatherClient.windZ/2F;
        super.tick();
        MinecraftClient client = MinecraftClient.getInstance();
        if (this.onGround || this.world.getBlockState(new BlockPos((int) this.x, (int)this.y, (int)this.z)).blocksMovement() || this.world.getFluidState(new BlockPos((int) this.x, (int) this.y, (int) this.z)).isIn(FluidTags.WATER) || this.world.getFluidState(new BlockPos((int) this.x, (int) this.y, (int) this.z)).isIn(FluidTags.LAVA)) {
            this.markDead();
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public record DefaultFactory(SpriteProvider provider) implements ParticleFactory<DefaultParticleType> {
        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double r, double g, double b) {
            return new SnowParticle(world, x, y, z, r, g, b, provider);
        }
    }
}
