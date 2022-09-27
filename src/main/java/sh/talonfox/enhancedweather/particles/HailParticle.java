package sh.talonfox.enhancedweather.particles;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

public class HailParticle extends SpriteBillboardParticle {
    public HailParticle(ClientWorld clientWorld, double x, double y, double z, double r, double g, double b, SpriteProvider provider) {
        super(clientWorld, x, y, z, r, g, b);
        this.setSprite(provider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return null;
    }
}
