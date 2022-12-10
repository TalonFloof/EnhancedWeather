package sh.talonfox.enhancedweather.common.particles;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ParticleRegister {
    public static final DefaultParticleType CLOUD = FabricParticleTypes.simple(true);
    public static final DefaultParticleType HAIL = FabricParticleTypes.simple(true);
    public static void Initialize() {
        Registry.register(Registries.PARTICLE_TYPE, new Identifier("enhancedweather", "cloud"), CLOUD);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier("enhancedweather", "hail"), HAIL);
    }
    public static void InitializeClient() {
        ParticleFactoryRegistry.getInstance().register(CLOUD, CloudParticle.DefaultFactory::new);
        ParticleFactoryRegistry.getInstance().register(HAIL, HailParticle.DefaultFactory::new);
    }
}
