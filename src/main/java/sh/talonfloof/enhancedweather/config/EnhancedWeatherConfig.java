package sh.talonfloof.enhancedweather.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "enhancedweather")
public class EnhancedWeatherConfig implements ConfigData {
    @ConfigEntry.Category("client")
    @Comment("We recommended disabling this if you're using shaders\n(Default: true)")
    public static boolean Client_ParticleRain = true;
    @ConfigEntry.Category("client")
    @Comment("We recommended disabling this if you're using shaders\n(Default: true)")
    public static boolean Client_ShowRainbow = true;
    @ConfigEntry.Category("client")
    @Comment("Changes the radius of clouds that are rendered")
    @ConfigEntry.BoundedDiscrete(min=9,max=16)
    public static int Client_CloudRadius = 9;

    @ConfigEntry.Category("weather")
    @Comment("Enabling this reduces the amount of rain fronts\n(Default: false)")
    public static boolean Weather_ReducedRainFronts = false;

    @ConfigEntry.Category("weather")
    @Comment("The minimum wind speed that tornadoes can spawn and exist in\n(Default: 50)")
    @ConfigEntry.BoundedDiscrete(min=0,max=100)
    public static int Weather_TornadoMinimumWind = 50;

    @ConfigEntry.Category("weather")
    @Comment("The chance (1 in [number]) of a tornado spawning in the right conditions\n(Default: 100)")
    public static int Weather_TornadoSpawnChance = 100;

    @ConfigEntry.Category("wind")
    @Comment("The chance (1 in [number]) of a low wind event occurring\n(Default: 4000)")
    public static int Wind_LowWindChance = 20*200;

    @ConfigEntry.Category("wind")
    @Comment("The chance (1 in [number]) of a high wind event occurring\n(Default: 8000)")
    public static int Wind_HighWindChance = 20*400;

    @ConfigEntry.Category("wind")
    @Comment("Base Time (in ticks) that a low wind event will last\n(Default: 2400)")
    public static int Wind_LowWindBaseTime = (20*60*2);

    @ConfigEntry.Category("wind")
    @Comment("Maximum extra time (in ticks) that a low wind event will last\n(Default: 12000)")
    public static int Wind_LowWindExtraTime = 20*60*10;

    @ConfigEntry.Category("wind")
    @Comment("Base Time (in ticks) that a high wind event will last\n(Default: 2400)")
    public static int Wind_HighWindBaseTime = (20*60*2);

    @ConfigEntry.Category("wind")
    @Comment("Maximum extra time (in ticks) that a high wind event will last\n(Default: 12000)")
    public static int Wind_HighWindExtraTime = 20*60*10;

    @ConfigEntry.Category("wind")
    @Comment("The lowest wind speed that a low wind event will drop to\n(Default: 5)")
    @ConfigEntry.BoundedDiscrete(min=0,max=100)
    public static int Wind_LowWindThreshold = 5;
}
