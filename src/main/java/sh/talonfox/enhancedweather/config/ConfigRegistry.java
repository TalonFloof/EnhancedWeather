package sh.talonfox.enhancedweather.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import sh.talonfox.enhancedweather.EnhancedWeather;


public class ConfigRegistry {
    public static void init() {
        AutoConfig.register(EnhancedWeatherConfig.class, JanksonConfigSerializer::new);
        EnhancedWeather.CONFIG = AutoConfig.getConfigHolder(EnhancedWeatherConfig.class).getConfig();
    }
}
