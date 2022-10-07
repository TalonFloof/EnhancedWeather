package sh.talonfox.enhancedweather.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import sh.talonfox.enhancedweather.common.particles.ParticleRegister;

@Environment(EnvType.CLIENT)
public class EnhancedweatherClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ParticleRegister.InitializeClient();
        ClientEvents.Initialize();
    }
}
