package sh.talonfox.enhancedweather.weather.weatherevents;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import sh.talonfox.enhancedweather.Enhancedweather;
import sh.talonfox.enhancedweather.network.UpdateStorm;
import sh.talonfox.enhancedweather.weather.Manager;
import sh.talonfox.enhancedweather.weather.Weather;

import java.util.*;

public class SquallLine extends Weather {
    public HashMap<Integer, UUID> Storms = new HashMap<>();
    /*
    Intensity Legend:
    Intensity 0: Proto-Squall Line (few, largely spaced storms)
    Intensity 1: Squall Line (fully formed, with each cell having a 50% change of having wind)
    Intensity 2: Severe Squall Line (All cells have strong wind, potential of supercell forming tornado)
     */
    public int Intensity = 0;
    public int MaxIntensity = 0;
    public float IntensityProgression = 0F;
    public boolean PeakedIntensity = false;
    public float MovementAngle;
    protected int ticks;

    @Override
    public Identifier getID() {
        return new Identifier("enhancedweather","squall_line");
    }

    public SquallLine(Manager manager, Vec3d pos) {
        HostManager = manager;
        Position = pos;
        MovementAngle = new Random().nextFloat(360);
        ticks = 0;
    }

    @Override
    public void deconstructor() {
        for(UUID i : Storms.values()) {
            if(HostManager.Weathers.containsKey(i)) {
                for (ServerPlayerEntity j : PlayerLookup.all(Objects.requireNonNull(HostManager.getWorld().getServer()))) {
                    UpdateStorm.send(HostManager.getWorld().getServer(), i, null, j);
                }
                HostManager.Weathers.remove(i);
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
            assert storm.Position.y == 200;
            var uuid = UUID.randomUUID();
            storm.Thundering = true;
            storm.Water = Enhancedweather.CONFIG.Weather_MinimumWaterToPrecipitate*2;
            storm.Precipitating = true;
            storm.SquallLineControlled = true;
            storm.Size = 300;
            storm.Angle = MovementAngle;
            if(Intensity == 0 && Storms.size() % 2 == 1) {
                storm.Placeholder = true;
            }
            HostManager.enqueueWeatherObject(uuid,storm);
            Storms.put(Storms.keySet().size(),uuid);
        }
        ticks++;
        if(ticks % 60 == 0) {
            if(Intensity != MaxIntensity) {
                if(Intensity >= MaxIntensity) {
                    PeakedIntensity = true;
                }
                IntensityProgression += 0.02F;
                if (IntensityProgression >= 0.6F) {
                    Intensity += 1;
                    IntensityProgression = 0;
                }
            }
            for(int i : Storms.keySet()) {
                if(HostManager.Weathers.containsKey(Storms.get(i))) {
                    HostManager.Weathers.get(Storms.get(i)).Position = calculateCellPosition(i);
                    if(Intensity >= 1 && i % 2 == 1) {
                        ((Cloud)HostManager.Weathers.get(Storms.get(i))).Placeholder = false;
                    } else if(Intensity == 0 && i % 2 == 1) {
                        ((Cloud)HostManager.Weathers.get(Storms.get(i))).Placeholder = true;
                    }
                }
            }
        }
        double vecX = -Math.sin(Math.toRadians(MovementAngle));
        double vecZ = Math.cos(Math.toRadians(MovementAngle));
        Vec3d motion = new Vec3d((vecX * (0.2F * 0.2F))/2, 0, (vecZ * (0.2F * 0.2F))/2);
        Position = Position.add(motion);
    }

    public Vec3d calculateCellPosition(int index) {
        return new Vec3d(-Math.sin(Math.toRadians(MovementAngle-90)),0, Math.cos(Math.toRadians(MovementAngle-90))).multiply((300*index)-150-(300*3)).add(Position);
    }

    @Override
    public NbtCompound generateUpdate() {
        NbtCompound data = super.generateUpdate();
        data.putInt("Intensity",Intensity);
        data.putInt("MaxIntensity",MaxIntensity);
        data.putFloat("IntensityProgression",IntensityProgression);
        data.putBoolean("PeakedIntensity",PeakedIntensity);
        data.putFloat("MovementAngle",MovementAngle);
        return data;
    }

    @Override
    public void applyUpdate(NbtCompound data) {
        super.applyUpdate(data);
        Intensity = data.getInt("Intensity");
        MaxIntensity = data.getInt("MaxIntensity");
        IntensityProgression = data.getFloat("IntensityProgression");
        PeakedIntensity = data.getBoolean("PeakedIntensity");
        MovementAngle = data.getFloat("MovementAngle");
    }

    @Override
    public JsonObject generateSaveDataJson() {
        JsonObject json = super.generateSaveDataJson();
        json.put("Intensity", new JsonPrimitive(Intensity));
        json.put("MaxIntensity", new JsonPrimitive(MaxIntensity));
        json.put("IntensityProgression",new JsonPrimitive(IntensityProgression));
        json.put("PeakedIntensity",new JsonPrimitive(PeakedIntensity));
        json.put("MovementAngle",new JsonPrimitive(MovementAngle));
        var storms = new JsonObject();
        for(int i : Storms.keySet()) {
            storms.put(Integer.toString(i),new JsonPrimitive(Storms.get(i).toString()));
        }
        json.put("Storms",storms);
        return json;
    }

    @Override
    public void applySaveDataJson(JsonObject json) {
        super.applySaveDataJson(json);
        Intensity = json.getInt("Intensity",0);
        MaxIntensity = json.getInt("MaxIntensity",0);
        IntensityProgression = json.getFloat("IntensityProgression",0);
        PeakedIntensity = json.getBoolean("PeakedIntensity",false);
        MovementAngle = json.getFloat("MovementAngle",0);
        var storms = json.getObject("Storms");
        assert storms != null;
        for(String i : storms.keySet()) {
            Storms.put(Integer.valueOf(i), UUID.fromString(((JsonPrimitive) Objects.requireNonNull(storms.get(i))).asString()));
        }
    }
}
