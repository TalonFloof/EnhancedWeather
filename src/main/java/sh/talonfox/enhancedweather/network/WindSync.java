package sh.talonfox.enhancedweather.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import sh.talonfox.enhancedweather.Enhancedweather;

public class WindSync {
    public static Identifier PACKET_ID = new Identifier("enhancedweather","wind_s2c_sync");
    /*
    PACKET BUFFER STRUCTURE
    int: Dimension ID
    float: Global Wind Angle
    float: Global Wind Speed
    int: Low Wind Timer
    int: High Wind Timer
     */
    public static void send(MinecraftServer server, int dimid) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(dimid);
        if(dimid == 0) {
            buf.writeFloat(Enhancedweather.WIND.AngleGlobal);
            buf.writeFloat(Enhancedweather.WIND.SpeedGlobal);
            buf.writeInt(Enhancedweather.WIND.LowWindTimer);
            buf.writeInt(Enhancedweather.WIND.HighWindTimer);
        } else if(dimid == -1) {
            buf.writeFloat(Enhancedweather.NETHER_WIND.AngleGlobal);
            buf.writeFloat(Enhancedweather.NETHER_WIND.SpeedGlobal);
            buf.writeInt(Enhancedweather.WIND.LowWindTimer);
            buf.writeInt(Enhancedweather.WIND.HighWindTimer);
        }
        for (ServerPlayerEntity player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, PACKET_ID, buf);
        }
    }
    public static void onReceive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        int dimid = packetByteBuf.readInt();
        if(dimid == 0) {
            Enhancedweather.CLIENT_WIND.AngleGlobal = packetByteBuf.readFloat();
            Enhancedweather.CLIENT_WIND.SpeedGlobal = packetByteBuf.readFloat();
            Enhancedweather.CLIENT_WIND.LowWindTimer = packetByteBuf.readInt();
            Enhancedweather.CLIENT_WIND.HighWindTimer = packetByteBuf.readInt();
        } else if(dimid == -1) {
            Enhancedweather.NETHER_CLIENT_WIND.AngleGlobal = packetByteBuf.readFloat();
            Enhancedweather.NETHER_CLIENT_WIND.SpeedGlobal = packetByteBuf.readFloat();
            Enhancedweather.NETHER_CLIENT_WIND.LowWindTimer = packetByteBuf.readInt();
            Enhancedweather.NETHER_CLIENT_WIND.HighWindTimer = packetByteBuf.readInt();
        }
    }
}
