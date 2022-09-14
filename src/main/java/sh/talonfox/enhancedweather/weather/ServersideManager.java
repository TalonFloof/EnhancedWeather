package sh.talonfox.enhancedweather.weather;

import blue.endless.jankson.JsonObject;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import sh.talonfox.enhancedweather.Enhancedweather;
import sh.talonfox.enhancedweather.network.UpdateCloud;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ServersideManager extends Manager {
    public int nextID = 0;
    private long ticks = 0;
    private final ServerWorld world;
    private Random rand;
    public ServersideManager(ServerWorld w) {
        this.world = w;
        this.rand = new Random();
    }

    @Override
    public void tick() {
        Clouds.values().stream().forEach(Cloud::tickServer);
        ticks++;
        if (ticks % 20 == 0) {
            if (world.getServer().getCurrentPlayerCount() == 0 && !Clouds.isEmpty()) {
                Clouds.clear();
            } else {
                if (ticks % 40 == 0) {
                    for (int j : Clouds.keySet()) {
                        Cloud cloud = Clouds.get(j);
                        for (ServerPlayerEntity i : PlayerLookup.around(world,new Vec3d(cloud.Position.x, 50, cloud.Position.z),1024.0D)) {
                            UpdateCloud.send(world.getServer(), j, Clouds.get(j).generateUpdate(), i);
                        }
                    }
                }
            }
            for (ServerPlayerEntity ent : PlayerLookup.all(Objects.requireNonNull(getWorld().getServer()))) {
                if (Clouds.size() < 20 * getWorld().getServer().getCurrentPlayerCount()) {
                    if (rand.nextInt(5) == 0) {
                        attemptCloudSpawn(ent, 200);
                    }
                }
            }
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
            soClose = getClosestCloud(Vec3d.ofCenter(tryPos), 300, -1, false);
            playerClose = ent.getWorld().getClosestPlayer(spawnX, 50, spawnZ, closestToPlayer, false);
        }
        if (soClose == null) {
            Cloud so = new Cloud(this,Vec3d.ofCenter(tryPos));
            int index = nextID;
            Clouds.put(index,so);
            nextID += 1;
            for(ServerPlayerEntity i : PlayerLookup.all(world.getServer())) {
                UpdateCloud.send(world.getServer(), index, so.generateUpdate(), i);
            }
        }
    }

    @Override
    public World getWorld() {
        return world;
    }

    public void save(MinecraftServer server) {
        JsonObject jsonObject = new JsonObject();
        String data = jsonObject.toJson(true,true);
        File file = new File(server.getSavePath(WorldSavePath.ROOT).toAbsolutePath() + "/enhancedweather/Clouds_DIM0.json5");
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
