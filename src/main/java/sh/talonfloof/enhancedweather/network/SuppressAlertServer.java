package sh.talonfloof.enhancedweather.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static sh.talonfloof.enhancedweather.block.RadarBlock.LIGHT;

public class SuppressAlertServer {
    public static final Identifier PACKET_ID = new Identifier("enhancedweather","suppress_alert");
    public static void onReceive(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity, ServerPlayNetworkHandler ignored, PacketByteBuf packetByteBuf, PacketSender ignored2) {
        BlockPos pos = packetByteBuf.readBlockPos();
        serverPlayerEntity.getServerWorld().setBlockState(pos,serverPlayerEntity.getServerWorld().getBlockState(pos).with(LIGHT,false));
    }
}
