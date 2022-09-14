package sh.talonfox.enhancedweather;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.world.dimension.DimensionTypes;
import sh.talonfox.enhancedweather.weather.ServersideManager;
import sh.talonfox.enhancedweather.wind.Wind;

public class ServerEvents {
    public static void Initialize() {
        ServerWorldEvents.LOAD.register((server,world) -> {
            if(world.getDimensionKey().equals(DimensionTypes.OVERWORLD)) {
                Enhancedweather.LOGGER.info("Server starting!");
                Enhancedweather.LOGGER.info("Initializing Wind...");
                Enhancedweather.WIND = new Wind();
                Enhancedweather.NETHER_WIND = new Wind();
                Enhancedweather.WIND.load(server, 0);
                Enhancedweather.NETHER_WIND.load(server, -1);
                Enhancedweather.LOGGER.info("Initializing Server-side Weather Manager...");
                Enhancedweather.SERVER_WEATHER = new ServersideManager(world);
                world.setWeather(Integer.MAX_VALUE,Integer.MAX_VALUE,false,false);
            }
        });
        ServerTickEvents.START_SERVER_TICK.register((server) -> {
            Enhancedweather.WIND.tick(server, 0);
            Enhancedweather.NETHER_WIND.tick(server, -1);
            Enhancedweather.SERVER_WEATHER.tick();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
            Enhancedweather.LOGGER.info("Server stopping!");
            Enhancedweather.LOGGER.info("Saving Enhanced Weather Data to world");
            Enhancedweather.WIND.save(server,0);
            Enhancedweather.NETHER_WIND.save(server,-1);
            Enhancedweather.SERVER_WEATHER.save(server);
            Enhancedweather.SERVER_WEATHER = null;
            Enhancedweather.NETHER_WIND = null;
            Enhancedweather.WIND = null;
        });
    }
}
