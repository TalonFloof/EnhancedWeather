package sh.talonfox.enhancedweather.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import sh.talonfox.enhancedweather.common.particles.ParticleRegister;
import sh.talonfox.enhancedweather.network.NetworkRegister;

@Environment(EnvType.CLIENT)
public class EnhancedWeatherClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ParticleRegister.InitializeClient();
        NetworkRegister.Initialize();
        ClientEvents.Initialize();
    }
}
