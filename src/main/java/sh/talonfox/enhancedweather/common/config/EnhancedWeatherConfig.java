package sh.talonfox.enhancedweather.common.config;


import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "enhanced-weather")
public class EnhancedWeatherConfig implements ConfigData {
    ////////// CLIENT //////////
    @ConfigEntry.Category("client")
    @Comment("Enables Fog that forms when it rains")
    public boolean Client_PrecipitationFog = false;
    @ConfigEntry.Category("client")
    @Comment("Enables new particle-based weather effects (This feature hasn't been implemented yet!)")
    public boolean Client_ParticlePrecipitation = false;
    @ConfigEntry.Category("client")
    @Comment("Makes particles move with the wind if enabled")
    public boolean Client_ParticleWind = true;
    @ConfigEntry.Category("client")
    @Comment("Makes non-storm clouds translucent when enabled")
    public boolean Client_TranslucentClouds = true;
    @ConfigEntry.Category("client")
    @Comment("Changes the render distance of the cloud particles. Only adjust this if your computer can't handle the particles")
    public double Client_CloudParticleRenderDistance = 512.0;
    ////////// WIND //////////
    @ConfigEntry.Category("wind")
    @ConfigEntry.BoundedDiscrete(min=0,max=100)
    @Comment("The minimum wind speed, can be a number from 0-100")
    public int Wind_MinimumSpeed = 0;
    @ConfigEntry.Category("wind")
    @ConfigEntry.BoundedDiscrete(min=0,max=100)
    @Comment("The maximum wind speed, can be a number from 0-100")
    public int Wind_MaximumSpeed = 100;
    @ConfigEntry.Category("wind")
    @Comment("The chance of a low wind event occurring")
    public int Wind_LowWindStartChance = 20*200;
    @ConfigEntry.Category("wind")
    @Comment("The chance of a high wind event occurring")
    public int Wind_HighWindStartChance = 20*400;
    @ConfigEntry.Category("wind")
    @Comment("The minimum duration (in ticks) of a low wind event")
    public int Wind_LowWindDurationBase = 20*60*2;
    @ConfigEntry.Category("wind")
    @Comment("The minimum duration (in ticks) of a high wind event")
    public int Wind_HighWindDurationBase = 20*60*2;
    @ConfigEntry.Category("wind")
    @Comment("The amount of maximum extra ticks that can be added to the base duration of a low wind event")
    public int Wind_LowWindDurationExtra = 20*60*10;
    @ConfigEntry.Category("wind")
    @Comment("The amount of maximum extra ticks that can be added to the base duration of a high wind event")
    public int Wind_HighWindDurationExtra = 20*60*10;
    ///////// WEATHER /////////
    @ConfigEntry.Category("weather")
    @Comment("If enabled, a random weather condition will occur in a new world")
    public boolean Weather_RandomConditionInNewWorld = true;
    @ConfigEntry.Category("weather")
    @Comment("If enabled, supercells can form tornadoes")
    public boolean Weather_TornadoesCanSpawn = true;
    @ConfigEntry.Category("weather")
    @Comment("Changes the chance of a lightning strike occurring")
    public int Weather_LightningStrikeBaseChance = 25000;
    @ConfigEntry.Category("weather")
    @Comment("Adjusts the minimum amount of water a cloud needs to collect for it to start precipitating")
    public int Weather_MinimumWaterToPrecipitate = 100;
    @ConfigEntry.Category("weather")
    @Comment("The chance of a cloud collecting water from nothing")
    public int Weather_WaterCollectionFromNothingChance = 100;
    @ConfigEntry.Category("weather")
    @Comment("The chance of a cloud collecting water from a humid biome")
    public int Weather_WaterCollectionFromBiomeChance = 15;
    @ConfigEntry.Category("weather")
    @Comment("The chance of a precipitation capable cloud precipitating")
    public int Weather_PrecipitationChance = 50;
    @ConfigEntry.Category("weather")
    @Comment("The chance of a isolated supercell forming (This chance is rolled daily)")
    public int Weather_SupercellChance = 15;
    @ConfigEntry.Category("weather")
    @Comment("The chance of a squall line forming (This chance is rolled daily)")
    public int Weather_SquallLineChance = 15;
}
