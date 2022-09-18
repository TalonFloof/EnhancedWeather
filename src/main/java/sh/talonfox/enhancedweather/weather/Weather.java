package sh.talonfox.enhancedweather.weather;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

public abstract class Weather {
    Manager HostManager;
    Vec3d Position = null;
    int Size = 1;
    int MaxSize = 300;

    public void tickClient() {

    }

    public void tickServer() {

    }

    public NbtCompound generateUpdate() {
        NbtCompound data = new NbtCompound();
        data.putDouble("X",Position.getX());
        data.putDouble("Y",Position.getY());
        data.putDouble("Z",Position.getZ());
        data.putInt("Size",Size);
        return data;
    }

    public void applyUpdate(NbtCompound data) {
        Position = new Vec3d(data.getDouble("X"),data.getDouble("Y"),data.getDouble("Z"));
        Size = data.getInt("Size");
    }

    public JsonObject generateSaveDataJson() {
        JsonObject json = new JsonObject();
        json.put("X",new JsonPrimitive(Position.getX()));
        json.put("Y",new JsonPrimitive(Position.getY()));
        json.put("Z",new JsonPrimitive(Position.getZ()));
        json.put("Size",new JsonPrimitive(Size));
        return json;
    }

    public void applySaveDataJson(JsonObject json) {
        Position = new Vec3d(json.getDouble("X",0),json.getDouble("Y",0),json.getDouble("Z",0));
        Size = json.getInt("Size",0);
    }
}
