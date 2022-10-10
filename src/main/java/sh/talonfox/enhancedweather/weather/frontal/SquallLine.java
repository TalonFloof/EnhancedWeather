package sh.talonfox.enhancedweather.weather.frontal;

import net.minecraft.util.math.Vec3d;
import sh.talonfox.enhancedweather.weather.Cloud;
import sh.talonfox.enhancedweather.weather.Manager;
import sh.talonfox.enhancedweather.weather.Weather;

import java.util.ArrayList;

public class SquallLine extends Weather {
    public ArrayList<Cloud> Storms = new ArrayList<Cloud>();

    public SquallLine(Manager manager, Vec3d pos) {
        HostManager = manager;
        Position = pos;
    }
}
