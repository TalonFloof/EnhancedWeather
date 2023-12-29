package sh.talonfox.enhancedweather.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import sh.talonfox.enhancedweather.EnhancedWeather;
import sh.talonfox.enhancedweather.EnhancedWeatherClient;


public class UpdateConditionsClient {
    public static void onReceive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        EnhancedWeatherClient.windX = packetByteBuf.readFloat();
        EnhancedWeatherClient.windZ = packetByteBuf.readFloat();
    }
}
