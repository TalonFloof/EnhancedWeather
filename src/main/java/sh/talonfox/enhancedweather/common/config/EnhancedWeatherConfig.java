package sh.talonfox.enhancedweather.common.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.client.gui.screen.Screen;

@Config(name = "enhancedweather")
public class EnhancedWeatherConfig implements ConfigData {
    ////////// CLIENT //////////
    @ConfigEntry.Category("client")
    public boolean Client_PrecipitationFog = false;
    @ConfigEntry.Category("client")
    public boolean Client_ParticlePrecipitation = false;
    @ConfigEntry.Category("client")
    public boolean Client_ParticleWind = true;
    @ConfigEntry.Category("client")
    public boolean Client_TranslucentClouds = true;
    @ConfigEntry.Category("client")
    public double Client_CloudParticleRenderDistance = 512.0;
    ////////// WIND //////////
    @ConfigEntry.Category("wind")
    @ConfigEntry.BoundedDiscrete(min=0,max=100)
    public int Wind_MinimumSpeed = 0;
    @ConfigEntry.Category("wind")
    @ConfigEntry.BoundedDiscrete(min=0,max=100)
    public int Wind_MaximumSpeed = 100;
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
    public boolean Weather_TornadoesCanSpawn = true;
    @ConfigEntry.Category("weather")
    public int Weather_LightningStrikeBaseChance = 25000;
    @ConfigEntry.Category("weather")
    public int Weather_MinimumWaterToPrecipitate = 100;
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
