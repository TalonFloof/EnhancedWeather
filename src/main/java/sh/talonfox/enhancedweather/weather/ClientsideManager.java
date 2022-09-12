package sh.talonfox.enhancedweather.weather;

import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;

public class ClientsideManager extends Manager {
    @Override
    public void tick() {
        for (Cloud i : Clouds.values()) {
            i.tickClient();
        }
    }

    @Override
    public World getWorld() {
        return MinecraftClient.getInstance().world;
    }
}
