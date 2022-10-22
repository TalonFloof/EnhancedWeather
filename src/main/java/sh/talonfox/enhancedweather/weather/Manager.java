package sh.talonfox.enhancedweather.weather;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import sh.talonfox.enhancedweather.weather.weatherevents.Cloud;

import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;

public abstract class Manager {
    public HashMap<UUID, Weather> Weathers = new HashMap<>();
    HashMap<UUID, Weather> EnqueuedWeather = new HashMap<>();
    public abstract void tick();
    public abstract World getWorld();

    public void enqueueWeatherObject(UUID id, Weather w) {
        EnqueuedWeather.put(id, w);
    }

    public Cloud getClosestCloud(Vec3d parPos, double maxDist, boolean rain, boolean thunder, boolean strongWinds, boolean supercell, int hailIntensityMin) {
        return (Cloud) Weathers.values().stream()
                .filter(so -> so instanceof Cloud)
                .filter(so -> !(((Cloud)so).Placeholder) && (((Cloud)so).Precipitating || !rain) && (((Cloud)so).Thundering || !thunder) && (((Cloud)so).WindIntensity > 0 || !strongWinds) && (((Cloud)so).Supercell || !supercell) && (hailIntensityMin == -1 || ((Cloud)so).HailIntensity >= hailIntensityMin))
                .filter(so -> so.Position.distanceTo(parPos) < maxDist)
                .min(Comparator.comparing(so -> so.Position.distanceTo(parPos))).orElse(null);
    }
}
