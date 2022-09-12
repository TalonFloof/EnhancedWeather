package sh.talonfox.enhancedweather.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import sh.talonfox.enhancedweather.Enhancedweather;
import sh.talonfox.enhancedweather.weather.Cloud;

public class UpdateCloud {
    public static Identifier PACKET_ID = new Identifier("enhancedweather","update_cloud_s2c");
    /*
    PACKET BUFFER STRUCTURE
    int: Cloud ID
    NbtCompound: Data (Null if deleting cloud)
     */
    public static void send(MinecraftServer server, int id, NbtCompound data, ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(id);
        buf.writeNbt(data);
        ServerPlayNetworking.send(player, PACKET_ID, buf);
    }
    public static void onReceive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        int id = packetByteBuf.readInt();
        NbtCompound data = packetByteBuf.readNbt();
        client.executeSync(() -> {
            if(data == null) {
                Enhancedweather.CLIENT_WEATHER.Clouds.remove(id);
            } else {
                if(Enhancedweather.CLIENT_WEATHER.Clouds.containsKey(id)) {
                    Enhancedweather.CLIENT_WEATHER.Clouds.get(id).applyUpdate(data);
                } else {
                    Cloud c = new Cloud(Enhancedweather.CLIENT_WEATHER,new Vec3d(0,0,0));
                    c.applyUpdate(data);
                    Enhancedweather.CLIENT_WEATHER.Clouds.put(id,c);
                }
            }
        });
    }
}
