package sh.talonfloof.enhancedweather.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import sh.talonfloof.enhancedweather.events.TornadoClient;
import sh.talonfloof.enhancedweather.EnhancedWeatherClient;

import java.util.UUID;

public class UpdateEventClient {
    public static void onReceive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        long lower = packetByteBuf.readLong();
        long upper = packetByteBuf.readLong();
        UUID id = new UUID(upper,lower);
        NbtCompound data = packetByteBuf.readNbt();
        client.send(() -> {
            if(data == null) {
                EnhancedWeatherClient.clientEvents.remove(id);
            } else {
                if(EnhancedWeatherClient.clientEvents.containsKey(id)) {
                    EnhancedWeatherClient.clientEvents.get(id).applyUpdate(data);
                } else {
                    if(data.getString("id").equals("enhancedweather:tornado")) {
                        TornadoClient t = new TornadoClient(0,0,0);
                        t.applyUpdate(data);
                        EnhancedWeatherClient.clientEvents.put(id,t);
                    }
                }
            }
        });
    }
}
