package sh.talonfloof.enhancedweather.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import sh.talonfloof.enhancedweather.WindManager;

public class UpdateConditions {
    public static Identifier PACKET_ID = new Identifier("enhancedweather","update_conditions");
    public static void send(MinecraftServer server, ServerPlayerEntity player, float windX, float windZ, double cloudX, double cloudZ) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(windX);
        buf.writeFloat(windZ);
        buf.writeFloat(WindManager.windSpeed);
        buf.writeDouble(cloudX);
        buf.writeDouble(cloudZ);
        ServerPlayNetworking.send(player, PACKET_ID, buf);
    }
}
