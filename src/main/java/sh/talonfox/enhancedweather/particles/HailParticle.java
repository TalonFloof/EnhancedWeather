package sh.talonfox.enhancedweather.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class HailParticle extends SpriteBillboardParticle {
    public HailParticle(ClientWorld clientWorld, double x, double y, double z, double r, double g, double b, SpriteProvider provider) {
        super(clientWorld, x, y, z, r, g, b);
        this.setSprite(provider);
        this.scale = 0.25F;
        this.alpha = 1F;
        this.maxAge = 1000;
        this.velocityX = 0;
        this.velocityY = -3F;
        this.velocityZ = 0;
        this.red = 0.5F;
        this.green = 0.5F;
        this.blue = 0.5F;
        this.angle = (float)Math.toRadians(clientWorld.getRandom().nextBetween(0,360));
        this.collidesWithWorld = true;
    }

    @Override
    public void tick() {
        this.velocityX = 0;
        this.velocityY = -3F;
        this.velocityZ = 0;
        super.tick();
        this.velocityX = 0;
        this.velocityY = -3F;
        this.velocityZ = 0;
        this.angle += Math.toRadians(2);
        if(this.onGround) {
            this.world.playSound(MinecraftClient.getInstance().player,x,y,z, SoundEvents.BLOCK_STONE_STEP, SoundCategory.AMBIENT,3F,5F);
            this.markDead();
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(EnvType.CLIENT)
    public record DefaultFactory(SpriteProvider provider) implements ParticleFactory<DefaultParticleType> {
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double r, double g, double b) {
            return new HailParticle(world, x, y, z, r, g, b, provider);
        }
    }
}
