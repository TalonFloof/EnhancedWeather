package sh.talonfox.enhancedweather.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import sh.talonfox.enhancedweather.EnhancedWeather;
import sh.talonfox.enhancedweather.EnhancedWeatherClient;

public class UpdateConditionsClient {
    public static void onReceive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        EnhancedWeatherClient.heat = packetByteBuf.readFloat();
        EnhancedWeatherClient.humidity = packetByteBuf.readFloat();
        EnhancedWeather.LOGGER.info("Heat: {}, Humidity: {}%", EnhancedWeatherClient.heat, Math.floor(EnhancedWeatherClient.humidity*100)/100);
    }
}
