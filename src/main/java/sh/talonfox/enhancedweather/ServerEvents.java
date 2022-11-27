package sh.talonfox.enhancedweather;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.dimension.DimensionTypes;
import sh.talonfox.enhancedweather.weather.weatherevents.Cloud;
import sh.talonfox.enhancedweather.weather.ServersideManager;
import sh.talonfox.enhancedweather.wind.Wind;

import java.util.Random;

public class ServerEvents {
    public static void Initialize() {
        ServerWorldEvents.LOAD.register((server,world) -> {
            if(world.getDimensionKey().equals(DimensionTypes.OVERWORLD)) {
                EnhancedWeather.LOGGER.info("Server starting!");
                EnhancedWeather.LOGGER.info("Initializing Wind...");
                EnhancedWeather.WIND = new Wind();
                EnhancedWeather.NETHER_WIND = new Wind();
                EnhancedWeather.WIND.load(server, 0);
                EnhancedWeather.NETHER_WIND.load(server, -1);
                EnhancedWeather.LOGGER.info("Initializing Server-side Weather Manager...");
                EnhancedWeather.SERVER_WEATHER = new ServersideManager(world);
                EnhancedWeather.SERVER_WEATHER.load(server);
                world.setWeather(Integer.MAX_VALUE,Integer.MAX_VALUE,false,false);
            }
        });
        ServerTickEvents.START_SERVER_TICK.register((server) -> {
            EnhancedWeather.WIND.tick(server, 0);
            EnhancedWeather.NETHER_WIND.tick(server, -1);
            EnhancedWeather.SERVER_WEATHER.tick();
        });
        ServerPlayConnectionEvents.JOIN.register((handler, packetSender, server) -> {
            if(ServersideManager.IsNewWorld) {
                ServersideManager.IsNewWorld = false;
                Random rand = new Random();
                int num = rand.nextInt(100);
                if(num < 33) {
                    EnhancedWeather.LOGGER.info("World will start out with clear weather");
                } else if(num < 66) {
                    EnhancedWeather.LOGGER.info("World will start out with rainy weather");
                    while(EnhancedWeather.SERVER_WEATHER.Weathers.size() < 20) {
                        EnhancedWeather.SERVER_WEATHER.attemptCloudSpawn(handler.getPlayer(), 200);
                    }
                    EnhancedWeather.SERVER_WEATHER.Weathers.values().stream().filter(so -> so instanceof Cloud).forEach((so) -> {
                        ((Cloud)so).Water = 400;
                        ((Cloud)so).Precipitating = true;
                    });
                } else {
                    EnhancedWeather.LOGGER.info("World will start out with stormy weather");
                    while(EnhancedWeather.SERVER_WEATHER.Weathers.size() < 20) {
                        EnhancedWeather.SERVER_WEATHER.attemptCloudSpawn(handler.getPlayer(), 200);
                    }
                    EnhancedWeather.SERVER_WEATHER.Weathers.values().stream().filter(so -> so instanceof Cloud).forEach((so) -> {
                        ((Cloud)so).Water = 400;
                        ((Cloud)so).Precipitating = true;
                        ((Cloud)so).Thundering = true;
                        ((Cloud)so).MaxHailIntensity = 0;
                        ((Cloud)so).MaxTornadoStage = 0;
                        ((Cloud)so).Supercell = false;
                    });
                }
            }
        });
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
            EnhancedWeather.LOGGER.info("Server stopping!");
            EnhancedWeather.LOGGER.info("Saving Enhanced Weather Data to world");
            EnhancedWeather.WIND.save(server,0);
            EnhancedWeather.NETHER_WIND.save(server,-1);
            EnhancedWeather.SERVER_WEATHER.save(server);
            EnhancedWeather.SERVER_WEATHER = null;
            EnhancedWeather.NETHER_WIND = null;
            EnhancedWeather.WIND = null;
            ServersideManager.IsNewWorld = false;
        });
    }
}
