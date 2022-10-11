package sh.talonfox.enhancedweather.weather.frontal;

import net.minecraft.util.math.Vec3d;
import sh.talonfox.enhancedweather.weather.Manager;
import sh.talonfox.enhancedweather.weather.Weather;

import java.util.ArrayList;
import java.util.UUID;

public class SquallLine extends Weather {
    public ArrayList<UUID> Storms = new ArrayList<UUID>();
    /*
    Intensity Legend:
    Intensity 0: Proto-Squall Line (few, largely spaced storms)
    Intensity 1: Squall Line (fully formed, with each cell having a 50% change of having wind)
     */
    public int Intensity = 0;
    public int MaxIntensity = 0;

    public SquallLine(Manager manager, Vec3d pos) {
        HostManager = manager;
        Position = pos;
    }
}
