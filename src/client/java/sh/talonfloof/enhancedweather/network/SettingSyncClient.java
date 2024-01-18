package sh.talonfloof.enhancedweather.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import sh.talonfloof.enhancedweather.CloudRenderManager;
import sh.talonfloof.enhancedweather.api.EnhancedWeatherAPI;

public class SettingSyncClient {
    public static void onReceive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        boolean reducedRain = packetByteBuf.readBoolean();
        if(EnhancedWeatherAPI.clientReducedRain != reducedRain) {
            EnhancedWeatherAPI.clientReducedRain = reducedRain;
            CloudRenderManager.forceUpdate();
        }

    }
}
