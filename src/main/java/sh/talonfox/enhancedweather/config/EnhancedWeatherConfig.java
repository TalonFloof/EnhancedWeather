package sh.talonfox.enhancedweather.config;

import blue.endless.jankson.Comment;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.client.gui.screen.Screen;

@Config(name = "enhancedweather")
public class EnhancedWeatherConfig implements ConfigData {
    ////////// WIND //////////
    @ConfigEntry.Category("wind")
    public int Wind_LowWindStartChance = 20*200;
    @ConfigEntry.Category("wind")
    public int Wind_HighWindStartChance = 20*400;
    @ConfigEntry.Category("wind")
    public int Wind_LowWindDurationBase = 20*60*2;
    @ConfigEntry.Category("wind")
    public int Wind_HighWindDurationBase = 20*60*2;
    @ConfigEntry.Category("wind")
    public int Wind_LowWindDurationExtra = 20*60*10;
    @ConfigEntry.Category("wind")
    public int Wind_HighWindDurationExtra = 20*60*10;
    ///////// WEATHER /////////
    @ConfigEntry.Category("weather")
    public boolean Weather_RandomConditionInNewWorld = true;
    @ConfigEntry.Category("weather")
    public int Weather_LightningStrikeBaseChance = 10000;
    @ConfigEntry.Category("weather")
    public int Weather_WaterCollectionFromNothingChance = 100;
    @ConfigEntry.Category("weather")
    public int Weather_WaterCollectionFromBiomeChance = 15;
    @ConfigEntry.Category("weather")
    public int Weather_PrecipitationChance = 50;

    public static Screen buildScreen(Screen parent) {
        return AutoConfig.getConfigScreen(EnhancedWeatherConfig.class, parent).get();
    }
}
