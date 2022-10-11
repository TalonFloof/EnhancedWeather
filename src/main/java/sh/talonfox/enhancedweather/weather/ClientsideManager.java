package sh.talonfox.enhancedweather.weather;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ClientsideManager extends Manager {
    public static float PrecipitationRate = 0F;
    public static float PrecipitationRateTarget = 0F;
    public static int PrecipitationIntensity = 0;
    private int ticks = 0;
    @Override
    public void tick() {
        Ambience.HighWindExists = false;
        for (Weather i : Clouds.values()) {
            i.tickClient();
        }
        Ambience.tick();
        ticks++;
        if(ticks % 40 == 0) {
            if(MinecraftClient.getInstance().player != null) {
                Cloud cloud = this.getClosestCloud(new Vec3d(MinecraftClient.getInstance().player.getX(),200,MinecraftClient.getInstance().player.getZ()),384,true, false, false, false, -1);
                if(cloud != null) {
                    PrecipitationRateTarget = 1F;
                    PrecipitationIntensity = cloud.TornadoStage!=Float.MIN_VALUE?cloud.TornadoStage+4:(cloud.Thundering?1:0);
                } else {
                    PrecipitationRateTarget = 0F;
                }
            }
        }
        if(PrecipitationRate > PrecipitationRateTarget) {
            PrecipitationRate -= 0.0005F;
        } else if(PrecipitationRate < PrecipitationRateTarget) {
            PrecipitationRate += 0.0005F;
        }
        if(PrecipitationRateTarget == 0 && PrecipitationRate != 0F && Math.abs(PrecipitationRate-PrecipitationRateTarget) < 0.001F) {
            PrecipitationRate = 0F;
            PrecipitationIntensity = 0;
        }
    }

    @Override
    public World getWorld() {
        return MinecraftClient.getInstance().world;
    }
}
