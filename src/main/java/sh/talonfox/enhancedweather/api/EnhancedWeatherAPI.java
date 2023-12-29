package sh.talonfox.enhancedweather.api;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import sh.talonfox.enhancedweather.EnhancedWeather;
import sh.talonfox.enhancedweather.util.ImageSampler;

import java.util.ArrayList;
import java.util.List;

public class EnhancedWeatherAPI {
    private static final ImageSampler FRONT_SAMPLE = new ImageSampler("data/enhancedweather/clouds/rain_fronts.png");
    private static final ImageSampler MAIN_SHAPE_SAMPLE = new ImageSampler("data/enhancedweather/clouds/main_shape.png");
    private static final ImageSampler LARGE_DETAILS_SAMPLE = new ImageSampler("data/enhancedweather/clouds/large_details.png");
    private static final ImageSampler VARIATION_SAMPLE = new ImageSampler("data/enhancedweather/clouds/variation.png");
    private static final ImageSampler RAIN_DENSITY = new ImageSampler("data/enhancedweather/clouds/rain_density.png");
    private static final ImageSampler THUNDERSTORMS = new ImageSampler("data/enhancedweather/clouds/thunderstorms.png");
    private static final float[] CLOUD_SHAPE = new float[64];
    private static final Vec2f[] OFFSETS;

    public static float getCoverage(float rainFront) {
        return MathHelper.lerp(rainFront, 1.3F, 0.5F);
    }

    public static float getCloudDensity(int x, int y, int z, float rainFront) {


        float density = MAIN_SHAPE_SAMPLE.sample(x * 0.75F, z * 0.75F);
        density += LARGE_DETAILS_SAMPLE.sample(x * 2.5F, z * 2.5F);

        density -= VARIATION_SAMPLE.sample(y * 2.5F, x * 2.5F) * 0.05F;
        density -= VARIATION_SAMPLE.sample(z * 2.5F, y * 2.5F) * 0.05F;
        density -= VARIATION_SAMPLE.sample(z * 2.5F, x * 2.5F) * 0.05F;

        int value = (int) (MathHelper.hashCode(x, y, z) % 3);
        density -= value * 0.01F;

        float density1 = density - CLOUD_SHAPE[MathHelper.clamp(y << 1, 0, 63)];
        float density2 = density + MAIN_SHAPE_SAMPLE.sample(x * 1.5F, z * 1.5F) - CLOUD_SHAPE[MathHelper.clamp(y, 0, 63)] * 3F;

        return MathHelper.lerp(rainFront, density1, density2);
    }

    public static float sampleFront(int x, int z, double scale) {
        float front = FRONT_SAMPLE.sample(x * scale, z * scale);
        scale *= 0.7;
        front *= RAIN_DENSITY.sample(x * scale, z * scale);
        return front;
    }

    public static float getWindX(ServerWorld world) {
        return EnhancedWeather.windXNoise.GetNoise((float) (world.getTime() / 600),0) * 2F;
    }

    public static float getWindZ(ServerWorld world) {
        return EnhancedWeather.windZNoise.GetNoise((float) (world.getTime() / 600),0) * 2F;
    }

    static {
        for (byte i = 0; i < 16; i++) {
            CLOUD_SHAPE[i] = (16 - i) / 16F;
            CLOUD_SHAPE[i] *= CLOUD_SHAPE[i];
        }
        for (byte i = 16; i < 64; i++) {
            CLOUD_SHAPE[i] = (i - 16) / 48F;
            CLOUD_SHAPE[i] *= CLOUD_SHAPE[i];
        }

        int radius = 6;
        int capacity = radius * 2 + 1;
        capacity *= capacity;

        List<Vec2f> offsets = new ArrayList<>(capacity);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) {
                    offsets.add(new Vec2f(x, z));
                }
            }
        }
        offsets.sort((v1, v2) -> {
            int d1 = (int)v1.x * (int)v1.x + (int)v1.y * (int)v1.y;
            int d2 = (int)v2.x * (int)v2.x + (int)v2.y * (int)v2.y;
            return Integer.compare(d1, d2);
        });
        OFFSETS = offsets.toArray(Vec2f[]::new);
    }
}
