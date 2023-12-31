package sh.talonfox.enhancedweather;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.random.Random;

public class WindManager {
    public static float windAngle = 0;
    public static float windSpeed = 0;
    public static float windSpeedChangeRate = 0.05F;
    public static int windSpeedRandChangeTimer = 0;
    public static int windSpeedRandChangeDelay = 10;
    public static int lowWindTimer = 0;
    public static int highWindTimer = 0;
    public static void reset() {
        Random rand = Random.create();
        windAngle = rand.nextInt(360)-180;
        windSpeed = rand.nextInt(45);
        windSpeedRandChangeTimer = 0;
        lowWindTimer = 0;
        highWindTimer = 0;
    }

    public static void tick() {
        Random rand = Random.create();
        if (lowWindTimer <= 0) {
            if (windSpeedRandChangeTimer-- <= 0) {
                if (highWindTimer <= 0)
                    windSpeed += (rand.nextDouble() * windSpeedChangeRate) - (windSpeedChangeRate / 2);
                else
                    windSpeed += (rand.nextDouble() * windSpeedChangeRate);
                windSpeedRandChangeTimer = windSpeedRandChangeDelay;
                if (highWindTimer <= 0)
                    if (rand.nextInt(20*200) == 0) {
                        lowWindTimer = (20*60*2) + rand.nextInt(20*60*10);
                        EnhancedWeather.LOGGER.info("Low Wind for {} ticks", lowWindTimer);
                    } else
                        lowWindTimer = 0;
                if (highWindTimer <= 0)
                    if (rand.nextInt(20*400) == 0) {
                        highWindTimer = (20*60*2) + rand.nextInt(20*60*10);
                        EnhancedWeather.LOGGER.info("High Wind for {} ticks", highWindTimer);
                    }
            }
        } else {
            lowWindTimer--;
            windSpeed -= 0.01F;
        }
        if (highWindTimer > 0) {
            highWindTimer--;
        }
        windAngle += (rand.nextFloat()) - (rand.nextFloat());
        if(windAngle < -180)
            windAngle += 360;
        if(windAngle > 180)
            windAngle -= 360;
        if (windSpeed < 0.00001F)
            windSpeed = 0.00001F;
        if (windSpeed > 100F)
            windSpeed = 100F;
    }
}
