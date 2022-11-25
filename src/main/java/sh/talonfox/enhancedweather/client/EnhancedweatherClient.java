package sh.talonfox.enhancedweather.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import sh.talonfox.enhancedweather.common.ItemGroupRegister;
import sh.talonfox.enhancedweather.common.config.ConfigRegister;
import sh.talonfox.enhancedweather.common.particles.ParticleRegister;
import sh.talonfox.enhancedweather.network.NetworkRegister;

@Environment(EnvType.CLIENT)
public class EnhancedweatherClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ConfigRegister.Initialize();
        ParticleRegister.InitializeClient();
        NetworkRegister.Initialize();
        ClientEvents.Initialize();
    }
}
