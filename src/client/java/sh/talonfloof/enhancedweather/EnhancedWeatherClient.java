package sh.talonfloof.enhancedweather;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import sh.talonfloof.enhancedweather.config.EnhancedWeatherConfig;
import sh.talonfloof.enhancedweather.events.WeatherEvent;
import sh.talonfloof.enhancedweather.network.*;
import sh.talonfloof.enhancedweather.particle.HailParticle;
import sh.talonfloof.enhancedweather.particle.SnowParticle;
import sh.talonfloof.enhancedweather.particle.TornadoParticle;
import sh.talonfloof.enhancedweather.network.*;
import sh.talonfloof.enhancedweather.particle.RainParticle;

import java.util.HashMap;
import java.util.UUID;

import static sh.talonfloof.enhancedweather.EnhancedWeather.*;

/*
Weather Modifier Values:
0.3F-0.4F Cloud Transition
0.4F-0.5F Cloud
0.5F-0.6F Rain Transition
0.6F-0.7F Rain
0.8F- Thunder
 */

public class EnhancedWeatherClient implements ClientModInitializer {
	public static float rain = 0F;
	public static float cloud = 0F;
	public static int wetness = 0;
	public static boolean showRainbow = false;
	public static float rainDest = 0F;
	public static float cloudDest = 0F;
	public static float windX = 0F;
	public static float windZ = 0F;
	public static float windSpeed = 0F;
	public static HashMap<UUID, WeatherEvent> clientEvents = new HashMap<>();

	@Override
	public void onInitializeClient() {
		//HudRenderCallback.EVENT.register(new EWDebugHud());
		ParticleFactoryRegistry.getInstance().register(EW_RAIN, RainParticle.DefaultFactory::new);
		ParticleFactoryRegistry.getInstance().register(EW_SNOW, SnowParticle.DefaultFactory::new);
		ParticleFactoryRegistry.getInstance().register(EW_HAIL, HailParticle.DefaultFactory::new);
		ParticleFactoryRegistry.getInstance().register(EW_TORNADO, TornadoParticle.DefaultFactory::new);
		ClientPlayNetworking.registerGlobalReceiver(UpdateConditions.PACKET_ID, UpdateConditionsClient::onReceive);
		ClientPlayNetworking.registerGlobalReceiver(ScreenOpen.PACKET_ID, ScreenOpenClient::onReceive);
		ClientPlayNetworking.registerGlobalReceiver(UpdateEvent.PACKET_ID, UpdateEventClient::onReceive);
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			EnhancedWeather.LOGGER.info("Client has disconnected");
			clientEvents.clear();
		});
		ClientTickEvents.START_WORLD_TICK.register((client) -> {
			for(UUID id : clientEvents.keySet()) {
				clientEvents.get(id).tickClient();
			}
			if(rainDest > 0.90) {
				wetness = Math.min(wetness + 1,1000);
				if(wetness >= 1000) {
					showRainbow = true;
				}
			} else {
				wetness = Math.max(wetness - 1,0);
				if(wetness == 0) {
					showRainbow = false;
				}
			}
			if(rain > rainDest) {
				rain -= 0.005F;
			} else if(rain < rainDest) {
				rain += 0.005F;
			}
			if(rain > 1) {
				rain = 1;
			}
			if(rainDest == 0 && rain != 0F && Math.abs(rain-rainDest) < 0.2F) {
				rain = 0F;
			}
			if(cloud > cloudDest) {
				cloud -= 0.005F;
			} else if(cloud < cloudDest) {
				cloud += 0.005F;
			}
			if(cloudDest == 0 && cloud != 0F && Math.abs(cloud-cloudDest) < 0.2F) {
				cloud = 0F;
			}
		});
		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			if(rain < 0.2F)
				return;
			if (client.isPaused() || client.world == null && client.getCameraEntity() == null)
				return;
			if(client.player.getY() > client.world.getDimensionEffects().getCloudsHeight())
				return;
			if(client.world.getBiome(client.player.getBlockPos()).value().getPrecipitation(client.player.getBlockPos()) == Biome.Precipitation.SNOW) {
				if(rain > 0) {
					for (int i = 0; i < 32; i++) {
						client.world.addParticle(EnhancedWeather.EW_SNOW, MathHelper.lerp(client.world.random.nextDouble(), client.player.getBlockX() - 64, client.player.getBlockX() + 64), client.player.getBlockY() + 50, MathHelper.lerp(client.world.random.nextDouble(), client.player.getBlockZ() - 64, client.player.getBlockZ() + 64), 0f, 0f, 0f);
					}
				}
			} else {
				int density = (int) ((rainDest == 1.0F ? 200 : 200) * rain);

				Random rand = Random.create();

				for (int pass = 0; pass < density; pass++) {

					float theta = (float) (2 * Math.PI * rand.nextFloat());
					float phi = (float) Math.acos(2 * rand.nextFloat() - 1);
					double x = 25 * MathHelper.sin(phi) * Math.cos(theta);
					double y = 25 * MathHelper.sin(phi) * Math.sin(theta);
					double z = 25 * MathHelper.cos(phi);

					var pos = new BlockPos.Mutable();
					pos.set(x + client.player.getX(), y + client.player.getY(), z + client.player.getZ());
					if (client.world.getTopY(Heightmap.Type.MOTION_BLOCKING, pos.getX(), pos.getZ()) > pos.getY())
						continue;

					if(EnhancedWeatherConfig.Client_ParticleRain)
						client.world.addParticle(EnhancedWeather.EW_RAIN, pos.getX() + rand.nextFloat(), pos.getY() + rand.nextFloat(), pos.getZ() + rand.nextFloat(), 0, 0, 0);
					if(windSpeed >= 50 && rainDest == 1F && pass < 5) {
						client.world.addParticle(EnhancedWeather.EW_HAIL, pos.getX() + rand.nextFloat(), pos.getY() + rand.nextFloat(), pos.getZ() + rand.nextFloat(), 0, 0, 0);
					}
				}
			}
		});
	}
}