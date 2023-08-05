package sh.talonfox.enhancedweather;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import sh.talonfox.enhancedweather.network.UpdateConditions;
import sh.talonfox.enhancedweather.network.UpdateConditionsClient;

/*
Weather Modifier Values:
0.3F-0.4F Cloud Transition
0.4F-0.5F Cloud
0.5F-0.6F Rain Transition
0.6F-0.7F Rain
0.8F- Thunder
 */

public class EnhancedWeatherClient implements ClientModInitializer {
	public static float heat = 0F;
	public static float humidity = 0F;
	public static float rain = 0F;
	public static float cloud = 0F;

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ClientPlayNetworking.registerGlobalReceiver(UpdateConditions.PACKET_ID, UpdateConditionsClient::onReceive);
	}
}