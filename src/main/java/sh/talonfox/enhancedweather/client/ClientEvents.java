package sh.talonfox.enhancedweather.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import sh.talonfox.enhancedweather.EnhancedWeather;
import sh.talonfox.enhancedweather.externmods.ExternalModRegistry;
import sh.talonfox.enhancedweather.externmods.journeymap.EnhancedWeatherJMPlugin;
import sh.talonfox.enhancedweather.weather.ClientsideManager;

public class ClientEvents {
    public static void Initialize() {
        ClientPlayConnectionEvents.INIT.register((handler,client) -> {
            EnhancedWeather.LOGGER.info("Client Connecting!");
            EnhancedWeather.CLIENT_WEATHER = new ClientsideManager();
        });
        ClientTickEvents.START_WORLD_TICK.register((client) -> {
            EnhancedWeather.CLIENT_WIND.tickClient();
            EnhancedWeather.NETHER_CLIENT_WIND.tickClient();
            EnhancedWeather.CLIENT_WEATHER.tick();
            if(ExternalModRegistry.containsExternalMod("journeymap")) {
                EnhancedWeatherJMPlugin.clientTickStart(client);
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler,client) -> {
            EnhancedWeather.LOGGER.info("Client Disconnecting!");
            EnhancedWeather.CLIENT_WEATHER = null;
        });
    }
}
