package sh.talonfloof.enhancedweather;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.dimension.DimensionTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.talonfloof.enhancedweather.api.EnhancedWeatherAPI;
import sh.talonfloof.enhancedweather.block.BlockRegistry;
import sh.talonfloof.enhancedweather.config.ConfigRegistry;
import sh.talonfloof.enhancedweather.config.EnhancedWeatherConfig;
import sh.talonfloof.enhancedweather.events.Tornado;
import sh.talonfloof.enhancedweather.events.WeatherEvent;
import sh.talonfloof.enhancedweather.network.SettingSync;
import sh.talonfloof.enhancedweather.network.SuppressAlertServer;
import sh.talonfloof.enhancedweather.network.UpdateConditions;
import sh.talonfloof.enhancedweather.network.UpdateEvent;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.UUID;

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
	public static HashMap<UUID, WeatherEvent> events = new HashMap<>();

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
				JsonObject events = jsonObject.getObject("events");
				for(String id : events.keySet()) {
					JsonObject eventData = events.getObject(id);
					if(eventData.get(String.class,"id").equals("enhancedweather:tornado")) {
						Tornado t = new Tornado(0,0,0,0);
						t.loadSaveData(eventData);
						EnhancedWeather.events.put(UUID.fromString(id),t);
					}
				}
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
		JsonObject eventObject = new JsonObject();
		jsonObject.put("DataFormat",new JsonPrimitive(EnhancedWeather.WEATHER_DATA_VERSION));
		jsonObject.put("cloudX",new JsonPrimitive(cloudX));
		jsonObject.put("cloudZ",new JsonPrimitive(cloudZ));
		jsonObject.put("windAngle",new JsonPrimitive(WindManager.windAngle));
		jsonObject.put("windSpeed",new JsonPrimitive(WindManager.windSpeed));
		jsonObject.put("lowWindTimer",new JsonPrimitive(WindManager.lowWindTimer));
		jsonObject.put("highWindTimer",new JsonPrimitive(WindManager.highWindTimer));
		for(UUID id : EnhancedWeather.events.keySet()) {
			WeatherEvent e = EnhancedWeather.events.get(id);
			eventObject.put(id.toString(),e.generateSaveData());
		}
		jsonObject.put("events",eventObject);
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
		ServerPlayNetworking.registerGlobalReceiver(SuppressAlertServer.PACKET_ID,SuppressAlertServer::onReceive);
		BlockRegistry.register();
		ServerWorldEvents.LOAD.register((server, world) -> {
			if(world.getDimensionKey().equals(DimensionTypes.OVERWORLD)) {
				events.clear();
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
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			SettingSync.send(handler.player);
		});
		ServerTickEvents.START_WORLD_TICK.register((world) -> {
			if(world.getDimensionKey().equals(DimensionTypes.OVERWORLD)) {
				if(events.size() < world.getServer().getCurrentPlayerCount()) {
					for (ServerPlayerEntity player : PlayerLookup.all(world.getServer())) {
						BlockPos pos = player.getBlockPos();
						Random r = Random.create();
						pos = pos.add(r.nextBetween(-1024, 1024), 0, r.nextBetween(-1024, 1024));
						if (EnhancedWeatherAPI.isThundering(world, 0, pos.getX() - MathHelper.floor(cloudX), pos.getZ() - MathHelper.floor(cloudZ)) && WindManager.windSpeed >= EnhancedWeatherConfig.Weather_TornadoMinimumWind) {
							if(r.nextInt(EnhancedWeatherConfig.Weather_TornadoSpawnChance) == 0) {
								Tornado t = new Tornado(pos.getX(),192,pos.getZ(),r.nextInt(3));
								events.put(UUID.randomUUID(),t);
								EnhancedWeather.LOGGER.info("Tornado Spawn: " + pos.getX() + ", " + pos.getZ());
							}
						}
					}
				}
				UUID[] ids = events.keySet().toArray(new UUID[0]);
				for(int i=0; i < ids.length; i++) {
					WeatherEvent e = events.get(ids[i]);
					if(e instanceof Tornado) {
						if(WindManager.windSpeed < EnhancedWeatherConfig.Weather_TornadoMinimumWind) {
							for (ServerPlayerEntity player : PlayerLookup.all(world.getServer())) {
								UpdateEvent.send(world.getServer(),ids[i],null,player);
							}
							events.remove(ids[i]);
						} else {
							var col = PlayerLookup.around(world.getServer().getOverworld(), new Vec3d(e.position.x, 50, e.position.z), 1024.0D);
							if (col.isEmpty()) {
								if(!(!world.getServer().isDedicated() && world.getServer().getCurrentPlayerCount() == 0)) {
									for (ServerPlayerEntity player : PlayerLookup.all(world.getServer())) {
										UpdateEvent.send(world.getServer(), ids[i], null, player);
									}
									events.remove(ids[i]);
								}
							} else {
								((Tornado) e).tickServer();
							}
						}
					}
				}
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
						SettingSync.send(player);
						UpdateConditions.send(world.getServer(), player, windX, windZ, cloudX, cloudZ);
						for(UUID id : events.keySet()) {
							UpdateEvent.send(world.getServer(),id,events.get(id).generateUpdate(),player);
						}
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