package sh.talonfox.enhancedweather.weather.frontal;

import net.minecraft.util.math.Vec3d;
import sh.talonfox.enhancedweather.weather.Manager;
import sh.talonfox.enhancedweather.weather.Weather;

import java.util.ArrayList;
import java.util.UUID;

public class SquallLine extends Weather {
    public ArrayList<UUID> Storms = new ArrayList<UUID>();

    public SquallLine(Manager manager, Vec3d pos) {
        HostManager = manager;
        Position = pos;
    }
}
