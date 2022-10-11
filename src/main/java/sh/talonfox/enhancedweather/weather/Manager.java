package sh.talonfox.enhancedweather.weather;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;

public abstract class Manager {
    public HashMap<UUID, Weather> Clouds = new HashMap<>();
    public abstract void tick();
    public abstract World getWorld();

    public Cloud getClosestCloud(Vec3d parPos, double maxDist, boolean rain, boolean thunder, boolean strongWinds, boolean supercell, int hailIntensityMin) {
        return (Cloud)Clouds.values().stream()
                .filter(so -> so instanceof Cloud)
                .filter(so -> (((Cloud)so).Precipitating || !rain) && (((Cloud)so).Thundering || !thunder) && (!strongWinds) && (((Cloud)so).Supercell || !supercell) && (hailIntensityMin == -1 || ((Cloud)so).HailIntensity >= hailIntensityMin))
                .filter(so -> so.Position.distanceTo(parPos) < maxDist)
                .min(Comparator.comparing(so -> so.Position.distanceTo(parPos))).orElse(null);
    }
}
