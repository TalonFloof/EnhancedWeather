package sh.talonfox.enhancedweather;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.talonfox.enhancedweather.block.BlockRegistry;
import sh.talonfox.enhancedweather.config.ConfigRegistry;
import sh.talonfox.enhancedweather.config.EnhancedWeatherConfig;
import sh.talonfox.enhancedweather.network.UpdateConditions;
import sh.talonfox.enhancedweather.util.FastNoiseLite;

import java.io.File;
import java.io.FileWriter;

public class EnhancedWeather implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("enhancedweather");
	public static final int WEATHER_DATA_VERSION = 202402001;
	public static EnhancedWeatherConfig CONFIG;
	public static final DefaultParticleType EW_RAIN = FabricParticleTypes.simple(true);
	public static final DefaultParticleType EW_SNOW = FabricParticleTypes.simple(true);
	public static final DefaultParticleType EW_HAIL = FabricParticleTypes.simple(true);
	public static final DefaultParticleType EW_TORNADO = FabricParticleTypes.simple(true);
	public static long noiseTick = 0;
	public static double cloudX = 0;
	public static double cloudZ = 0;

	public boolean load(MinecraftServer server) {
		new File(server.getSavePath(WorldSavePath.ROOT).toAbsolutePath() + "/enhancedweather/Clouds_DIM0.json5").delete();
		File file = new File(server.getSavePath(WorldSavePath.ROOT).toAbsolutePath() + "/enhancedweather/Weather_DIM0.json5");
		if(file.exists() && file.isFile()) {
			try {
				JsonObject jsonObject = Jankson.builder().build().load(file);
				if (jsonObject.getLong("DataFormat", 0L) != EnhancedWeather.WEATHER_DATA_VERSION) {
					return false;
				}
				cloudX = jsonObject.getDouble("cloudX",0);
				cloudZ = jsonObject.getDouble("cloudZ",0);
				WindManager.windAngle = jsonObject.getFloat("windAngle",0F);
				WindManager.windSpeed = jsonObject.getFloat("windSpeed",0F);
				WindManager.lowWindTimer = jsonObject.getInt("lowWindTimer",0);
				WindManager.highWindTimer = jsonObject.getInt("highWindTimer",0);
			} catch (Exception e) {
				EnhancedWeather.LOGGER.error("Failed to load Weather Data");
				EnhancedWeather.LOGGER.error("Reason: "+e.toString());
				return false;
			}
			return true;
		}
        return false;
    }

	public static void save(MinecraftServer server) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.put("DataFormat",new JsonPrimitive(EnhancedWeather.WEATHER_DATA_VERSION));
		jsonObject.put("cloudX",new JsonPrimitive(cloudX));
		jsonObject.put("cloudZ",new JsonPrimitive(cloudZ));
		jsonObject.put("windAngle",new JsonPrimitive(WindManager.windAngle));
		jsonObject.put("windSpeed",new JsonPrimitive(WindManager.windSpeed));
		jsonObject.put("lowWindTimer",new JsonPrimitive(WindManager.lowWindTimer));
		jsonObject.put("highWindTimer",new JsonPrimitive(WindManager.highWindTimer));
		String data = jsonObject.toJson(true,true);
		File file = new File(server.getSavePath(WorldSavePath.ROOT).toAbsolutePath() + "/enhancedweather/Weather_DIM0.json5");
		try {
			new File(file.getParent()).mkdir();
			file.delete();
			file.createNewFile();
			FileWriter stream = new FileWriter(file);
			stream.write(data);
			stream.close();
		} catch (Exception e) {
			EnhancedWeather.LOGGER.error("Failed to save Weather Data");
			EnhancedWeather.LOGGER.error("Reason: "+e.toString());
		}
	}

	@Override
	public void onInitialize() {
		ConfigRegistry.init();
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("enhancedweather", "rain"), EW_RAIN);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("enhancedweather", "snow"), EW_SNOW);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("enhancedweather", "hail"), EW_HAIL);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("enhancedweather", "tornado"), EW_TORNADO);
		BlockRegistry.register();
		ServerWorldEvents.LOAD.register((server, world) -> {
			if(world.getDimensionKey().equals(DimensionTypes.OVERWORLD)) {
				if (!load(server)) {
					EnhancedWeather.LOGGER.info("No data was found, generating initial values");
					WindManager.reset();
					Random r = Random.create();
					cloudX = r.nextBetween(-16777216, 16777216);
					cloudZ = r.nextBetween(-16777216, 16777216);
					EnhancedWeather.LOGGER.info("Starting cloud position will be: X={},Z={}", cloudX, cloudZ);
				} else {
					EnhancedWeather.LOGGER.info("Loaded Weather Data successfully");
				}
			}
		});
		ServerTickEvents.START_WORLD_TICK.register((world) -> {
			if(world.getDimensionKey().equals(DimensionTypes.OVERWORLD)) {
				world.setRainGradient(0F);
				world.setThunderGradient(0F);
				noiseTick += 1;
				WindManager.tick();
				float windX = (float)-Math.sin(Math.toRadians(WindManager.windAngle))*(WindManager.windSpeed/25F);
				float windZ = (float)Math.cos(Math.toRadians(WindManager.windAngle))*(WindManager.windSpeed/25F);
				float moveX = (float)-Math.sin(Math.toRadians(WindManager.windAngle))*Math.min(1.5F,WindManager.windSpeed/25F);
				float moveZ = (float)Math.cos(Math.toRadians(WindManager.windAngle))*Math.min(1.5F,WindManager.windSpeed/25F);
				cloudX += (moveX * 0.002) * 32;
				cloudZ += (moveZ * 0.002) * 32;
				if (noiseTick % 20 == 0) {
					for (ServerPlayerEntity player : PlayerLookup.all(world.getServer())) {
						UpdateConditions.send(world.getServer(), player, windX, windZ, cloudX, cloudZ);
					}
				}
			}
		});
		ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
			EnhancedWeather.LOGGER.info("Flushing data since server stop was triggered...");
			save(server);
		});
	}
}