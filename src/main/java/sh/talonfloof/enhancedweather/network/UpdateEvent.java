package sh.talonfloof.enhancedweather.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class UpdateEvent {
    public static Identifier PACKET_ID = new Identifier("enhancedweather","update_event");
    public static void send(MinecraftServer server, UUID id, NbtCompound data, ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeLong(id.getLeastSignificantBits());
        buf.writeLong(id.getMostSignificantBits());
        buf.writeNbt(data);
        ServerPlayNetworking.send(player, PACKET_ID, buf);
    }
}
