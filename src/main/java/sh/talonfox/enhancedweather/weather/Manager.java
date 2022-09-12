package sh.talonfox.enhancedweather.weather;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.HashMap;

public abstract class Manager {
    public HashMap<Integer, Cloud> Clouds = new HashMap<>();
    public abstract void tick();
    public abstract World getWorld();

    public Cloud getClosestCloud(Vec3d parPos, double maxDist, int severityFlagMin, boolean rain) {
        return Clouds.values().stream()
                .filter(so -> (so.Precipitating || !rain) && (severityFlagMin == -1 || 0 >= severityFlagMin))
                .filter(so -> so.Position.distanceTo(parPos) < maxDist)
                .min(Comparator.comparing(so -> so.Position.distanceTo(parPos))).orElse(null);
    }
}
