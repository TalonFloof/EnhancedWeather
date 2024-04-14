package sh.talonfloof.enhancedweather.config;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Modmenu(modId = "enhancedweather")
@Config(name = "enhancedweather", wrapperName="EnhancedWeatherConfig")
public class EnhancedWeatherConfigModel {
    @SectionHeader("client")
    //@Comment("We recommended disabling this if you're using shaders\n(Default: true)")
    public boolean Client_ParticleRain = true;
    //@Comment("We recommended disabling this if you're using shaders\n(Default: true)")
    public boolean Client_ShowRainbow = true;
    //@Comment("Changes the radius of clouds that are rendered\n(Default: 9)")
    @RangeConstraint(min=6,max=16)
    public int Client_CloudRadius = 9;
    public boolean Client_ParticlesMoveWithWind = true;

    @SectionHeader("weather")
    @Sync(value=Option.SyncMode.OVERRIDE_CLIENT)
    //@Comment("Enabling this reduces the amount of rain fronts\n(Default: false)")
    public boolean Weather_ReducedRainFronts = false;

    //@Comment("The minimum wind speed that tornadoes can spawn and exist in\n(Default: 50)")
    @RangeConstraint(min=0,max=100)
    public int Weather_TornadoMinimumWind = 50;

    //@Comment("The chance (1 in [number]) of a tornado spawning in the right conditions\n(Default: 100)")
    public int Weather_TornadoSpawnChance = 100;

    @SectionHeader("wind")
    //@Comment("The chance (1 in [number]) of a low wind event occurring\n(Default: 4000)")
    public int Wind_LowWindChance = 20*200;

    //@Comment("The chance (1 in [number]) of a high wind event occurring\n(Default: 8000)")
    public int Wind_HighWindChance = 20*400;

    //@Comment("Base Time (in ticks) that a low wind event will last\n(Default: 2400)")
    public int Wind_LowWindBaseTime = (20*60*2);

    //@Comment("Maximum extra time (in ticks) that a low wind event will last\n(Default: 12000)")
    public int Wind_LowWindExtraTime = 20*60*10;

    //@Comment("Base Time (in ticks) that a high wind event will last\n(Default: 2400)")
    public int Wind_HighWindBaseTime = (20*60*2);

    //@Comment("Maximum extra time (in ticks) that a high wind event will last\n(Default: 12000)")
    public int Wind_HighWindExtraTime = 20*60*10;

    //@Comment("The lowest wind speed that a low wind event will drop to\n(Default: 5)")
    @RangeConstraint(min=0,max=100)
    public int Wind_LowWindThreshold = 5;
    @SectionHeader("misc")
    public List<String> Misc_DimensionWhitelist = new ArrayList<>(List.of("minecraft:overworld"));
}
