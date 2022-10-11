package sh.talonfox.enhancedweather;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.dimension.DimensionTypes;
import sh.talonfox.enhancedweather.weather.Cloud;
import sh.talonfox.enhancedweather.weather.ServersideManager;
import sh.talonfox.enhancedweather.wind.Wind;

import java.util.Random;
import java.util.stream.Collectors;

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
                Enhancedweather.SERVER_WEATHER.load(server);
                world.setWeather(Integer.MAX_VALUE,Integer.MAX_VALUE,false,false);
            }
        });
        ServerTickEvents.START_SERVER_TICK.register((server) -> {
            Enhancedweather.WIND.tick(server, 0);
            Enhancedweather.NETHER_WIND.tick(server, -1);
            Enhancedweather.SERVER_WEATHER.tick();
        });
        ServerPlayConnectionEvents.JOIN.register((handler, packetSender, server) -> {
            if(ServersideManager.IsNewWorld) {
                ServersideManager.IsNewWorld = false;
                Random rand = new Random();
                int num = rand.nextInt(100);
                if(num < 33) {
                    Enhancedweather.LOGGER.info("World will start out with clear weather");
                } else if(num < 66) {
                    Enhancedweather.LOGGER.info("World will start out with rainy weather");
                    while(Enhancedweather.SERVER_WEATHER.Clouds.size() < 20) {
                        Enhancedweather.SERVER_WEATHER.attemptCloudSpawn(handler.getPlayer(), 200);
                    }
                    Enhancedweather.SERVER_WEATHER.Clouds.values().stream().filter(so -> so instanceof Cloud).forEach((so) -> {
                        ((Cloud)so).Water = 400;
                        ((Cloud)so).Precipitating = true;
                    });
                } else {
                    Enhancedweather.LOGGER.info("World will start out with stormy weather");
                    while(Enhancedweather.SERVER_WEATHER.Clouds.size() < 20) {
                        Enhancedweather.SERVER_WEATHER.attemptCloudSpawn(handler.getPlayer(), 200);
                    }
                    Enhancedweather.SERVER_WEATHER.Clouds.values().stream().filter(so -> so instanceof Cloud).forEach((so) -> {
                        ((Cloud)so).Water = 400;
                        ((Cloud)so).Precipitating = true;
                        ((Cloud)so).Thundering = true;
                    });
                }
            }
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
            ServersideManager.IsNewWorld = false;
        });
    }
}
