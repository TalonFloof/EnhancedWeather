package sh.talonfloof.enhancedweather.events;

import blue.endless.jankson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

public abstract class WeatherEvent {
    public Vec3d position;
    public WeatherEvent(double x, double y, double z) {
        position = new Vec3d(x,y,z);
    }
    public NbtCompound generateUpdate() {
        return new NbtCompound();
    }
    public void applyUpdate(NbtCompound nbt) {
        position = new Vec3d(nbt.getDouble("x"),nbt.getDouble("y"),nbt.getDouble("z"));
    }
    public JsonObject generateSaveData() {
        return new JsonObject();
    }
    public void loadSaveData(JsonObject data) {
        position = new Vec3d(data.getDouble("x",0),data.getDouble("y",0),data.getDouble("z",0));
    }
    public void tickClient() {}
    public void tickServer() {}
}
