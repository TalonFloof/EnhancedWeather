package sh.talonfox.enhancedweather;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.talonfox.enhancedweather.network.UpdateConditions;
import sh.talonfox.enhancedweather.util.FastNoiseLite;

public class EnhancedWeather implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("enhancedweather");
	public static FastNoiseLite heatNoise = new FastNoiseLite();
	public static FastNoiseLite humidityNoise = new FastNoiseLite();
	public static long noiseTick = 0;
	public static final DefaultParticleType EW_RAIN = FabricParticleTypes.simple(true);

	public static float getBaseHeat(ServerWorld world, int x, int z) {
		float temperature = MathHelper.clamp(world.getBiome(new BlockPos(x,64,z)).value().weather.temperature(),0F,1F);
		if(temperature < 0.8F) {
			return MathHelper.lerp(temperature/0.8F,0F,50F);
		} else {
			return MathHelper.lerp((temperature-0.8F)/0.2F,50F,95F);
		}
	}

	public static float getHeat(ServerWorld world, int x, int z) {
		float base = getBaseHeat(world,x,z);
		float time = (float)(Math.cos((2 * (((float)((world.getTimeOfDay()+6000)%24000))/24000F) + 1) * Math.PI) / 2 + 0.5) * 0.6F + 0.7F;
		return base * time * (heatNoise.GetNoise((float) (world.getTime() / 100),0)+1f);
	}

	public static float getBaseHumidity(ServerWorld world, int x, int z) {
		return world.getBiome(new BlockPos(x,64,z)).value().weather.downfall()*100F;
	}

	public static float getHumidity(ServerWorld world, int x, int z) {
		return (getBaseHumidity(world,x,z) * 0.7F + 40 * 0.3F) * (humidityNoise.GetNoise((float) (world.getTime() / 100),0)+1f);
	}

	@Override
	public void onInitialize() {
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("enhancedweather", "rain"), EW_RAIN);
		heatNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
		humidityNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
		heatNoise.SetFractalOctaves(2);
		humidityNoise.SetFractalOctaves(2);
		ServerWorldEvents.LOAD.register((server, world) -> {
			heatNoise.SetSeed(((int)world.getSeed()));
			humidityNoise.SetSeed((int)world.getSeed()+1);
		});
		ServerTickEvents.START_WORLD_TICK.register((world) -> {
			if(world.getDimensionKey().equals(DimensionTypes.OVERWORLD)) {
				world.setRainGradient(0F);
				world.setThunderGradient(0F);
				noiseTick += 1;
				if (noiseTick % 20 == 0) {
					for (ServerPlayerEntity player : PlayerLookup.all(world.getServer())) {
						UpdateConditions.send(world.getServer(), player, getHeat(world, player.getBlockX(), player.getBlockZ()), getHumidity(world, player.getBlockX(), player.getBlockZ()));
					}
				}
			}
		});
	}
}