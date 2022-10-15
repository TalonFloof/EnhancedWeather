package sh.talonfox.enhancedweather.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
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
import sh.talonfox.enhancedweather.common.particles.CloudParticle;
import sh.talonfox.enhancedweather.weather.weatherevents.Cloud;

import java.util.UUID;

public class UpdateStorm {
    public static Identifier PACKET_ID = new Identifier("enhancedweather","update_storm_s2c");
    /*
    PACKET BUFFER STRUCTURE
    long: Lower 64-bits of UUID
    long: Upper 64-bits of UUID
    NbtCompound: Data (Null if deleting cloud)
     */
    public static void send(MinecraftServer server, UUID id, NbtCompound data, ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeLong(id.getLeastSignificantBits());
        buf.writeLong(id.getMostSignificantBits());
        buf.writeNbt(data);
        ServerPlayNetworking.send(player, PACKET_ID, buf);
    }
    public static void onReceive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        long lower = packetByteBuf.readLong();
        long upper = packetByteBuf.readLong();
        UUID id = new UUID(upper,lower);
        NbtCompound data = packetByteBuf.readNbt();
        client.executeSync(() -> {
            if(data == null) {
                var storm = Enhancedweather.CLIENT_WEATHER.Weathers.get(id);
                if(storm != null) {
                    if(storm instanceof Cloud) {
                        ((Cloud) storm).ParticlesCloud.forEach(i -> ((CloudParticle)i).setAge(i.getMaxAge()-50));
                        ((Cloud) storm).ParticlesFunnel.forEach(i -> ((CloudParticle)i).setAge(i.getMaxAge()-50));
                    }
                    Enhancedweather.CLIENT_WEATHER.Weathers.remove(id);
                }
            } else {
                if(Enhancedweather.CLIENT_WEATHER.Weathers.containsKey(id)) {
                    Enhancedweather.CLIENT_WEATHER.Weathers.get(id).applyUpdate(data);
                } else {
                    Cloud c = new Cloud(Enhancedweather.CLIENT_WEATHER,new Vec3d(0,0,0));
                    c.applyUpdate(data);
                    Enhancedweather.CLIENT_WEATHER.Weathers.put(id,c);
                }
            }
        });
    }
}
