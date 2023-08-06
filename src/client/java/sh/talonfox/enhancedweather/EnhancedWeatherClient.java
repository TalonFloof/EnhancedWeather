package sh.talonfox.enhancedweather;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import sh.talonfox.enhancedweather.config.EnhancedWeatherConfig;
import sh.talonfox.enhancedweather.network.UpdateConditions;
import sh.talonfox.enhancedweather.network.UpdateConditionsClient;
import sh.talonfox.enhancedweather.particle.RainParticle;
import sh.talonfox.enhancedweather.particle.SnowParticle;

import static sh.talonfox.enhancedweather.EnhancedWeather.EW_RAIN;
import static sh.talonfox.enhancedweather.EnhancedWeather.EW_SNOW;

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
	public static float rainDest = 0F;
	public static float cloudDest = 0F;
	public static float windX = 0F;
	public static float windZ = 0F;
	public static boolean firstReceive = false;

	@Override
	public void onInitializeClient() {
		ParticleFactoryRegistry.getInstance().register(EW_RAIN, RainParticle.DefaultFactory::new);
		ParticleFactoryRegistry.getInstance().register(EW_SNOW, SnowParticle.DefaultFactory::new);
		ClientPlayNetworking.registerGlobalReceiver(UpdateConditions.PACKET_ID, UpdateConditionsClient::onReceive);
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> firstReceive = true);
		ClientTickEvents.START_WORLD_TICK.register((client) -> {
			if(rain > rainDest) {
				rain -= 0.0005F;
			} else if(rain < rainDest) {
				rain += 0.0005F;
			}
			if(rainDest == 0 && rain != 0F && Math.abs(rain-rainDest) < 0.001F) {
				rain = 0F;
			}
			if(cloud > cloudDest) {
				cloud -= 0.0005F;
			} else if(cloud < cloudDest) {
				cloud += 0.0005F;
			}
			if(cloudDest == 0 && cloud != 0F && Math.abs(cloud-cloudDest) < 0.001F) {
				cloud = 0F;
			}
		});
		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			if(!EnhancedWeatherConfig.Client_ParticleRain)
				return;
			if (client.isPaused() || client.world == null && client.getCameraEntity() == null)
				return;
			if(client.world.getBiome(client.player.getBlockPos()).value().getPrecipitation(client.player.getBlockPos()) == Biome.Precipitation.SNOW) {
				for(int i=0; i < 32; i++) {
					client.world.addParticle(EnhancedWeather.EW_SNOW, MathHelper.lerp(client.world.random.nextDouble(), client.player.getBlockX() - 64, client.player.getBlockX() + 64), client.player.getBlockY() + 50, MathHelper.lerp(client.world.random.nextDouble(), client.player.getBlockZ() - 64, client.player.getBlockZ() + 64), 0f, 0f, 0f);
				}
			} else {
				int density = (int) ((rainDest == 1.0F ? 800 : 200) * rain);

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

					client.world.addParticle(EnhancedWeather.EW_RAIN, pos.getX() + rand.nextFloat(), pos.getY() + rand.nextFloat(), pos.getZ() + rand.nextFloat(), 0, 0, 0);
				}
			}
		});
	}
}