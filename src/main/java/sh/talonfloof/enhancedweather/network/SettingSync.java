package sh.talonfloof.enhancedweather.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import sh.talonfloof.enhancedweather.config.EnhancedWeatherConfig;

public class SettingSync {
    public static Identifier PACKET_ID = new Identifier("enhancedweather","setting_sync");
    public static void send(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(EnhancedWeatherConfig.Weather_ReducedRainFronts);
        ServerPlayNetworking.send(player,PACKET_ID,buf);
    }
}
