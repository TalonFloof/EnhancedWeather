package sh.talonfloof.enhancedweather.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import sh.talonfloof.enhancedweather.CloudRenderManager;
import sh.talonfloof.enhancedweather.api.EnhancedWeatherAPI;
import sh.talonfloof.enhancedweather.EnhancedWeatherClient;


public class UpdateConditionsClient {
    public static void onReceive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        EnhancedWeatherClient.windX = packetByteBuf.readFloat();
        EnhancedWeatherClient.windZ = packetByteBuf.readFloat();
        EnhancedWeatherClient.windSpeed = packetByteBuf.readFloat();
        CloudRenderManager.cloudX = packetByteBuf.readDouble();
        CloudRenderManager.cloudZ = packetByteBuf.readDouble();
        if(client.player != null) {
            float rain = Math.max(0, EnhancedWeatherAPI.sampleFrontClient(client.player.getBlockX() - MathHelper.floor(CloudRenderManager.cloudX), client.player.getBlockZ() - MathHelper.floor(CloudRenderManager.cloudZ),0.1)-0.2F)/0.8F;
            boolean thunder = EnhancedWeatherAPI.isThunderingClient(client.world, 0, client.player.getBlockX() - MathHelper.floor(CloudRenderManager.cloudX), client.player.getBlockZ() - MathHelper.floor(CloudRenderManager.cloudZ));
            EnhancedWeatherClient.rainDest = thunder && rain >= 0.2F ? 1F : MathHelper.clamp(rain/0.2F,0F,0.99F);
            EnhancedWeatherClient.cloudDest = MathHelper.clamp(rain/0.2F,0,1);
        }
    }
}
