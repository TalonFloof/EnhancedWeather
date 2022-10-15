package sh.talonfox.enhancedweather.weather;

import net.minecraft.util.Identifier;
import sh.talonfox.enhancedweather.weather.weatherevents.Cloud;
import sh.talonfox.enhancedweather.weather.weatherevents.SquallLine;

public class WeatherRegistry {
    public static void Initialize() {
        Weather.register(new Identifier("enhancedweather","cloud"), Cloud::new);
        Weather.register(new Identifier("enhancedweather","squall_line"), SquallLine::new);
    }
}
