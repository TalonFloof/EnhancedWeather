package sh.talonfox.enhancedweather.common.particles;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ParticleRegister {
    public static final DefaultParticleType CLOUD = FabricParticleTypes.simple(true);
    public static final DefaultParticleType HAIL = FabricParticleTypes.simple(true);
    public static void Initialize() {
        Registry.register(Registry.PARTICLE_TYPE, new Identifier("enhancedweather", "cloud"), CLOUD);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier("enhancedweather", "hail"), HAIL);
    }
    public static void InitializeClient() {
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
            registry.register(new Identifier("enhancedweather", "particle/cloud2"));
        }));
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
            registry.register(new Identifier("enhancedweather", "particle/hail"));
        }));
        ParticleFactoryRegistry.getInstance().register(CLOUD, CloudParticle.DefaultFactory::new);
        ParticleFactoryRegistry.getInstance().register(HAIL, HailParticle.DefaultFactory::new);
    }
}
