package sh.talonfox.enhancedweather.weather;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ClientsideManager extends Manager {
    public static float PrecipitationRate = 0F;
    public static float PrecipitationRateTarget = 0F;
    private int ticks = 0;
    @Override
    public void tick() {
        for (Cloud i : Clouds.values()) {
            i.tickClient();
        }
        ticks++;
        if(ticks % 40 == 0) {
            if(MinecraftClient.getInstance().player != null) {
                Cloud cloud = this.getClosestCloud(new Vec3d(MinecraftClient.getInstance().player.getX(),200,MinecraftClient.getInstance().player.getZ()),384,0,true);
                if(cloud != null) {
                    PrecipitationRateTarget = 1F;
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
    }

    @Override
    public World getWorld() {
        return MinecraftClient.getInstance().world;
    }
}
