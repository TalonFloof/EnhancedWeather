package sh.talonfox.enhancedweather.events;

import net.minecraft.util.math.Vec3d;

public class WeatherEvent {
    public Vec3d position;
    public WeatherEvent(double x, double y, double z) {
        position = new Vec3d(x,y,z);
    }
}
