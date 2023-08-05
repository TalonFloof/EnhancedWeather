package sh.talonfox.enhancedweather.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import sh.talonfox.enhancedweather.EnhancedWeather;
import sh.talonfox.enhancedweather.EnhancedWeatherClient;

import static sh.talonfox.enhancedweather.EnhancedWeatherClient.firstReceive;

public class UpdateConditionsClient {
    public static void onReceive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        EnhancedWeatherClient.heat = packetByteBuf.readFloat();
        EnhancedWeatherClient.humidity = packetByteBuf.readFloat();
        EnhancedWeather.LOGGER.info("Heat: {}, Humidity: {}%", EnhancedWeatherClient.heat, Math.floor(EnhancedWeatherClient.humidity*100)/100);
        if(EnhancedWeatherClient.humidity > 50 && EnhancedWeatherClient.humidity < 65) {
            EnhancedWeatherClient.cloudDest = 1F;
            EnhancedWeatherClient.rainDest = 0.5F;
        } else if(EnhancedWeatherClient.humidity > 65) {
            EnhancedWeatherClient.cloudDest = 1F;
            EnhancedWeatherClient.rainDest = 1F;
        } else if(EnhancedWeatherClient.humidity > 40) {
            EnhancedWeatherClient.cloudDest = MathHelper.lerp(Math.min(1F,(EnhancedWeatherClient.humidity-40)/10F),0F,1F);
            EnhancedWeatherClient.rainDest = 0F;
        } else {
            EnhancedWeatherClient.cloudDest = 0F;
            EnhancedWeatherClient.rainDest = 0F;
        }
        if(firstReceive) {
            EnhancedWeatherClient.cloud = EnhancedWeatherClient.cloudDest;
            EnhancedWeatherClient.rain = EnhancedWeatherClient.rainDest;
            firstReceive = false;
        }
    }
}
