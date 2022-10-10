package sh.talonfox.enhancedweather.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class NetworkRegister {
    public static void Initialize() {
        ClientPlayNetworking.registerGlobalReceiver(WindSync.PACKET_ID, WindSync::onReceive);
        ClientPlayNetworking.registerGlobalReceiver(UpdateStorm.PACKET_ID, UpdateStorm::onReceive);
    }
}
