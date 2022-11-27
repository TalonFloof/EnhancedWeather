package sh.talonfox.enhancedweather.common.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import sh.talonfox.enhancedweather.EnhancedWeather;

public class ConfigRegister {
    public static void Initialize() {
        AutoConfig.register(EnhancedWeatherConfig.class, JanksonConfigSerializer::new);
        EnhancedWeather.CONFIG = AutoConfig.getConfigHolder(EnhancedWeatherConfig.class).getConfig();
    }
}
