package sh.talonfox.enhancedweather;

import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ModInitializer;
import sh.talonfox.enhancedweather.common.ItemGroupRegister;
import sh.talonfox.enhancedweather.common.blocks.BlockRegister;
import sh.talonfox.enhancedweather.common.config.ConfigRegister;
import sh.talonfox.enhancedweather.common.config.EnhancedWeatherConfig;
import sh.talonfox.enhancedweather.network.NetworkRegister;
import sh.talonfox.enhancedweather.common.particles.ParticleRegister;
import sh.talonfox.enhancedweather.weather.ClientsideManager;
import sh.talonfox.enhancedweather.weather.ServersideManager;
import sh.talonfox.enhancedweather.weather.WeatherRegistry;
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
    public static EnhancedWeatherConfig CONFIG = OmegaConfig.register(EnhancedWeatherConfig.class);
    public static final Logger LOGGER = LoggerFactory.getLogger("enhancedweather");

    public static final long WEATHER_DATA_VERSION = 202242001;

    @Override
    public void onInitialize() {
        BlockRegister.Initialize();
        ItemGroupRegister.Initialize();
        ParticleRegister.Initialize();
        WeatherRegistry.Initialize();
        ServerEvents.Initialize();
        CommandsRegister.Initialize();
    }
}
