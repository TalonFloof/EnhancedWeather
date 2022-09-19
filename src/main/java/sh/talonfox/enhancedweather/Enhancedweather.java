package sh.talonfox.enhancedweather;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import sh.talonfox.enhancedweather.client.ClientEvents;
import sh.talonfox.enhancedweather.config.ConfigRegister;
import sh.talonfox.enhancedweather.config.EnhancedWeatherConfig;
import sh.talonfox.enhancedweather.network.NetworkRegister;
import sh.talonfox.enhancedweather.particles.ParticleRegister;
import sh.talonfox.enhancedweather.weather.ClientsideManager;
import sh.talonfox.enhancedweather.weather.ServersideManager;
import sh.talonfox.enhancedweather.wind.Wind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Enhancedweather implements ModInitializer {
    public static Wind WIND = null;
    public static Wind CLIENT_WIND = new Wind();
    public static Wind NETHER_WIND = null;
    public static Wind NETHER_CLIENT_WIND = new Wind();
    public static ServersideManager SERVER_WEATHER = null;
    public static ClientsideManager CLIENT_WEATHER = null;
    public static EnhancedWeatherConfig CONFIG;
    public static final Logger LOGGER = LoggerFactory.getLogger("enhancedweather");

    @Override
    public void onInitialize() {
        ConfigRegister.Initialize();
        ParticleRegister.Initialize();
        NetworkRegister.Initialize();
        ServerEvents.Initialize();
        CommandsRegister.Initialize();
    }
}
