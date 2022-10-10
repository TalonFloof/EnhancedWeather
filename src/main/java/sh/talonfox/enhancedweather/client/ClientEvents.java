package sh.talonfox.enhancedweather.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import sh.talonfox.enhancedweather.Enhancedweather;
import sh.talonfox.enhancedweather.weather.ClientsideManager;

public class ClientEvents {
    public static void Initialize() {
        ClientPlayConnectionEvents.INIT.register((handler,client) -> {
            Enhancedweather.LOGGER.info("Client Connecting!");
            Enhancedweather.CLIENT_WEATHER = new ClientsideManager();
        });
        ClientTickEvents.START_WORLD_TICK.register((client) -> {
            Enhancedweather.CLIENT_WIND.tickClient();
            Enhancedweather.NETHER_CLIENT_WIND.tickClient();
            Enhancedweather.CLIENT_WEATHER.tick();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler,client) -> {
            Enhancedweather.LOGGER.info("Client Disconnecting!");
            Enhancedweather.CLIENT_WEATHER = null;
        });
    }
}
