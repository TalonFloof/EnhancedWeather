package sh.talonfox.enhancedweather.config;

import blue.endless.jankson.Comment;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.client.gui.screen.Screen;

@Config(name = "enhancedweather")
public class EnhancedWeatherConfig implements ConfigData {
    @ConfigEntry.Category("enhancedweather.wind")
    @Comment("Sets the change of a Low Wind Event occurring (1 in a [value] chance). Defaults to 4000.")
    public int Wind_LowWindStartChance = 20*200;

    @ConfigEntry.Category("enhancedweather.wind")
    @Comment("Sets the change of a High Wind Event occurring (1 in a [value] chance). Defaults to 8000.")
    public int Wind_HighWindStartChance = 20*400;

    public static Screen buildScreen(Screen parent) {
        return AutoConfig.getConfigScreen(EnhancedWeatherConfig.class, parent).get();
    }
}
