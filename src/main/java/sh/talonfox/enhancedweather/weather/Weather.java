package sh.talonfox.enhancedweather.weather;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.function.BiFunction;

public abstract class Weather {
    static HashMap<Identifier, BiFunction<Manager, Vec3d, ? extends Weather>> RegisteredStorms = new HashMap<>();
    protected Manager HostManager;
    public Vec3d Position = null;
    public int Size = 1;
    int MaxSize = 300;

    public void deconstructor() {

    }

    public void tickClient() {

    }

    public void tickServer() {

    }

    public Identifier getID() {
        return new Identifier("enhancedweather","null");
    }

    public static void register(Identifier id, BiFunction<Manager, Vec3d, ? extends Weather> constructor) {
        RegisteredStorms.put(id,constructor);
    }

    public static Weather constructStorm(Identifier id, Manager manager, Vec3d pos) {
        if(RegisteredStorms.containsKey(id))
            return RegisteredStorms.get(id).apply(manager, pos);
        return null;
    }

    public NbtCompound generateUpdate() {
        NbtCompound data = new NbtCompound();
        data.putString("Identifier",getID().toString());
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
        json.put("Identifier",new JsonPrimitive(getID().toString()));
        json.put("X",new JsonPrimitive(Position.getX()));
        json.put("Y",new JsonPrimitive(Position.getY()));
        json.put("Z",new JsonPrimitive(Position.getZ()));
        json.put("Size",new JsonPrimitive(Size));
        return json;
    }

    public void applySaveDataJson(JsonObject json) {
        Position = new Vec3d(json.getDouble("X",0),json.getDouble("Y",200),json.getDouble("Z",0));
        Size = json.getInt("Size",0);
    }
}
