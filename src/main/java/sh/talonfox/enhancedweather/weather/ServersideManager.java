package sh.talonfox.enhancedweather.weather;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;

import blue.endless.jankson.JsonPrimitive;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import sh.talonfox.enhancedweather.Enhancedweather;
import sh.talonfox.enhancedweather.network.UpdateStorm;
import sh.talonfox.enhancedweather.weather.weatherevents.Cloud;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class ServersideManager extends Manager {
    private long ticks = 0;
    private long secondsSinceNoPlayers = 0;
    private final ServerWorld world;
    private Random rand;
    private long PreviousDay = 0;
    public static boolean IsNewWorld = false;
    public ServersideManager(ServerWorld w) {
        this.world = w;
        this.rand = new Random();
    }

    @Override
    public void tick() {
        Weathers.values().forEach(Weather::tickServer);
        ticks++;
        if(EnqueuedWeather.size() > 0) {
            EnqueuedWeather.keySet().forEach((id) -> {
                Weathers.put(id,EnqueuedWeather.get(id));
            });
            EnqueuedWeather.clear();
        }
        if (ticks % 20 == 0) {
            if (world.getServer().getCurrentPlayerCount() == 0 && !Weathers.isEmpty()) { // To prevent weather from despawning before entering a single player world
                if(secondsSinceNoPlayers >= 15) {
                    Weathers.clear();
                    secondsSinceNoPlayers = 0;
                } else {
                    secondsSinceNoPlayers += 1;
                }
            } else {
                secondsSinceNoPlayers = 0;
                if (ticks % 40 == 0) {
                    if(world.getServer().getCurrentPlayerCount() == 0)
                        return;
                    if((long)Math.floor(world.getTimeOfDay()/24000F) != PreviousDay) {
                        PreviousDay = (long)Math.floor(world.getTimeOfDay()/24000F);
                        var key_array = new ArrayList<UUID>(Weathers.keySet());
                        for (ServerPlayerEntity i : PlayerLookup.all(world.getServer())) {
                            if(Weathers.keySet().size() == 0)
                                continue;
                            Cloud cloud = null;
                            int dist = Integer.MAX_VALUE;
                            while(dist >= 1024) {
                                cloud = (Cloud) Weathers.get(key_array.get(rand.nextInt(key_array.size())));
                                if(Weathers.keySet().size() == 0)
                                    break;
                                if(cloud != null)
                                    dist = (int)Math.floor(new Vec3d(i.getX(),200,i.getZ()).distanceTo(cloud.Position));
                            }
                            if(new Random().nextInt(30) == 0 && cloud != null) {
                                cloud.Thundering = true;
                                cloud.HailIntensity = 0;
                                cloud.Supercell = true;
                                cloud.Precipitating = true;
                                cloud.Placeholder = false;
                                cloud.aimAtPlayer(i);
                            }
                        }
                    }
                    for (UUID j : Weathers.keySet()) {
                        var cloud = Weathers.get(j);
                        var col = PlayerLookup.around(world.getServer().getOverworld(), new Vec3d(cloud.Position.x, 50, cloud.Position.z), 1024.0D);
                        if (col.isEmpty()) {
                            if(cloud instanceof Cloud) {
                                if(((Cloud)cloud).SquallLineControlled)
                                    continue;
                            }
                            cloud.deconstructor();
                            for (ServerPlayerEntity i : PlayerLookup.all(world.getServer())) {
                                UpdateStorm.send(world.getServer(), j, null, i);
                            }
                            Weathers.remove(j);
                            break;
                        }
                        for (ServerPlayerEntity i : col) {
                            UpdateStorm.send(world.getServer(), j, Weathers.get(j).generateUpdate(), i);
                        }
                    }
                }
            }
            // Temporary
            /*for (ServerPlayerEntity ent : PlayerLookup.all(Objects.requireNonNull(getWorld().getServer()))) {
                if (Clouds.size() < 20 * getWorld().getServer().getCurrentPlayerCount()) {
                    if (rand.nextInt(5) == 0) {
                        attemptCloudSpawn(ent, 200);
                    }
                }
            }*/
        }
    }

    public void attemptCloudSpawn(PlayerEntity ent, int y) {
        int tryCountMax = 10;
        int tryCountCur = 0;
        int spawnX = -1;
        int spawnZ = -1;
        Vec3i tryPos = null;
        Cloud soClose = null;
        PlayerEntity playerClose = null;

        int closestToPlayer = 128;
        float windOffsetDist = Math.min(256, 1124 / 4 * 3);
        float angle = Enhancedweather.WIND.AngleGlobal;
        double vecX = -Math.sin(angle) * windOffsetDist;
        double vecZ = Math.cos(angle) * windOffsetDist;
        while (tryCountCur++ == 0 || (tryCountCur < tryCountMax && (soClose != null || playerClose != null))) {
            spawnX = (int) (ent.getX() - vecX + rand.nextInt(1024) - rand.nextInt(1024));
            spawnZ = (int) (ent.getZ() - vecZ + rand.nextInt(1024) - rand.nextInt(1024));
            tryPos = new Vec3i(spawnX, y, spawnZ);
            soClose = getClosestCloud(Vec3d.ofCenter(tryPos), 300, false, false, false, false, -1);
            playerClose = ent.getWorld().getClosestPlayer(spawnX, 50, spawnZ, closestToPlayer, false);
        }
        if (soClose == null) {
            Cloud so = new Cloud(this,Vec3d.ofCenter(tryPos));
            UUID id = UUID.randomUUID();
            Weathers.put(id,so);
            for(ServerPlayerEntity i : PlayerLookup.all(world.getServer())) {
                UpdateStorm.send(world.getServer(), id, so.generateUpdate(), i);
            }
        }
    }

    @Override
    public World getWorld() {
        return world;
    }

    public void load(MinecraftServer server) {
        File file = new File(server.getSavePath(WorldSavePath.ROOT).toAbsolutePath() + "/enhancedweather/Weather_DIM0.json5");
        if(file.exists() && file.isFile()) {
            try {
                JsonObject jsonObject = Jankson.builder().build().load(file);
                PreviousDay = jsonObject.getLong("previousDay",0);
                JsonObject storms = jsonObject.getObject("storms");
                if(storms != null) {
                    Weathers.clear();
                    for (String i : storms.keySet()) {
                        Weather storm = Weather.constructStorm(new Identifier(((JsonPrimitive) Objects.requireNonNull(Objects.requireNonNull(storms.getObject(i)).get("Identifier"))).asString()),this,new Vec3d(0,0,0));
                        storm.applySaveDataJson(Objects.requireNonNull(storms.getObject(i)));
                        Weathers.put(UUID.fromString(i),storm);
                    }
                }
            } catch (Exception e) {
                Enhancedweather.LOGGER.error("Failed to load Cloud Data for Dimension #0");
                Enhancedweather.LOGGER.error("Reason: "+e.toString());
            }
        }
    }

    public void save(MinecraftServer server) {
        JsonObject jsonObject = new JsonObject();
        JsonObject storms = new JsonObject();
        for(UUID i : Weathers.keySet()) {
            storms.put(i.toString(), Weathers.get(i).generateSaveDataJson());
        }
        jsonObject.put("DataFormat",new JsonPrimitive(Enhancedweather.WEATHER_DATA_VERSION));
        jsonObject.put("previousDay",new JsonPrimitive(PreviousDay));
        jsonObject.put("storms",storms);
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
            Enhancedweather.LOGGER.error("Failed to save Cloud Data for Dimension #0");
            Enhancedweather.LOGGER.error("Reason: "+e.toString());
        }
    }
}
