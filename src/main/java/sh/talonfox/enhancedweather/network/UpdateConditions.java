package sh.talonfox.enhancedweather.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class UpdateConditions {
    public static Identifier PACKET_ID = new Identifier("enhancedweather","update_conditions");
    public static void send(MinecraftServer server, ServerPlayerEntity player, float heat, float humidity) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(heat);
        buf.writeFloat(humidity);
        ServerPlayNetworking.send(player, PACKET_ID, buf);
    }
}
