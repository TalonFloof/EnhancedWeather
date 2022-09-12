package sh.talonfox.enhancedweather.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.util.ActionResult;
import sh.talonfox.enhancedweather.Enhancedweather;

public class ConfigRegister {
    public static void Initialize() {
        ConfigHolder<EnhancedWeatherConfig> config = AutoConfig.register(EnhancedWeatherConfig.class, JanksonConfigSerializer::new);
        Enhancedweather.CONFIG = config.getConfig();
        config.registerLoadListener((manager, data) -> {
            Enhancedweather.LOGGER.info("Config Reload!");
            Enhancedweather.CONFIG = data;
            return ActionResult.PASS;
        });
        config.registerSaveListener((manager, data) -> {
            try {
                data.validatePostLoad();
            } catch (ConfigData.ValidationException ignored) { }
            return ActionResult.PASS;
        });
    }
}
