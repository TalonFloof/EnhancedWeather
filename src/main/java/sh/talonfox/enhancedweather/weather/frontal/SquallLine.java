package sh.talonfox.enhancedweather.weather.frontal;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import sh.talonfox.enhancedweather.Enhancedweather;
import sh.talonfox.enhancedweather.network.UpdateStorm;
import sh.talonfox.enhancedweather.weather.Cloud;
import sh.talonfox.enhancedweather.weather.Manager;
import sh.talonfox.enhancedweather.weather.Weather;

import java.util.*;

public class SquallLine extends Weather {
    public HashMap<Integer, UUID> Storms = new HashMap<Integer, UUID>();
    /*
    Intensity Legend:
    Intensity 0: Proto-Squall Line (few, largely spaced storms)
    Intensity 1: Squall Line (fully formed, with each cell having a 50% change of having wind)
    Intensity 2: Severe Squall Line (All cells have strong wind, potential of supercell forming tornado)
     */
    public int Intensity = 0;
    public int MaxIntensity = 0;
    public boolean PeakedIntensity = false;
    public float MovementAngle = 0;

    public SquallLine(Manager manager, Vec3d pos) {
        HostManager = manager;
        Position = pos;
        MovementAngle = new Random().nextFloat(360);
    }

    @Override
    public void deconstructor() {
        for(UUID i : Storms.values()) {
            if(HostManager.Clouds.containsKey(i)) {
                for (ServerPlayerEntity j : PlayerLookup.all(Objects.requireNonNull(HostManager.getWorld().getServer()))) {
                    UpdateStorm.send(HostManager.getWorld().getServer(), i, null, j);
                }
                HostManager.Clouds.remove(i);
            }
        }
    }

    @Override
    public void tickClient() {

    }

    @Override
    public void tickServer() {
        if(Storms.size() < 8) {
            var storm = new Cloud(HostManager,calculateCellPosition(Storms.size()));
            var uuid = UUID.randomUUID();
            storm.Thundering = true;
            storm.Water = Enhancedweather.CONFIG.Weather_MinimumWaterToPrecipitate*2;
            storm.Precipitating = true;
            storm.SquallLineControlled = true;
            HostManager.Clouds.put(uuid,storm);
        }
    }

    public Vec3d calculateCellPosition(int index) {
        return new Vec3d(-Math.sin(Math.toRadians(MovementAngle-90)),0, Math.cos(Math.toRadians(MovementAngle-90))).multiply((150*index)-75-(150*3)).add(Position);
    }
}
