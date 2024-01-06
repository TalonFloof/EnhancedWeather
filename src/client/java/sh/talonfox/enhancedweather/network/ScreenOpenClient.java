package sh.talonfox.enhancedweather.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import sh.talonfox.enhancedweather.screen.RadarScreen;

import java.util.Timer;
import java.util.TimerTask;

public class ScreenOpenClient {
    public static void onReceive(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        BlockPos pos = packetByteBuf.readBlockPos();
        client.send(() -> client.setScreen(new RadarScreen(pos)));
    }
}
