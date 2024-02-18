package sh.talonfloof.enhancedweather.events;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import net.minecraft.nbt.NbtCompound;
import sh.talonfloof.enhancedweather.WindManager;

public class Tornado extends WeatherEvent {
    public int intensity = 0;
    public int intensityMax = 0;
    public int intensityProgress = 0;
    public Tornado(double x, double y, double z, int intensityMax) {
        super(x,y,z);
    }
    @Override
    public NbtCompound generateUpdate() {
        NbtCompound c = new NbtCompound();
        c.putString("id","enhancedweather:tornado");
        c.putDouble("x",position.x);
        c.putDouble("y",position.y);
        c.putDouble("z",position.z);
        c.putInt("intensity",intensity);
        c.putInt("intensityMax",intensityMax);
        return c;
    }
    @Override
    public JsonObject generateSaveData() {
        JsonObject c = new JsonObject();
        c.put("id",new JsonPrimitive("enhancedweather:tornado"));
        c.put("x",new JsonPrimitive(position.x));
        c.put("y",new JsonPrimitive(position.y));
        c.put("z",new JsonPrimitive(position.z));
        c.put("intensity",new JsonPrimitive(intensity));
        c.put("intensityMax",new JsonPrimitive(intensityMax));
        c.put("intensityProgression",new JsonPrimitive(intensityProgress));
        return c;
    }

    @Override
    public void loadSaveData(JsonObject data) {
        super.loadSaveData(data);
    }

    @Override
    public void tickServer() {
        float moveX = (float)-Math.sin(Math.toRadians(WindManager.windAngle))*Math.min(1.5F,WindManager.windSpeed/25F);
        float moveZ = (float)Math.cos(Math.toRadians(WindManager.windAngle))*Math.min(1.5F,WindManager.windSpeed/25F);
        position = position.add((moveX * 0.002) * 32,0,(moveZ * 0.002) * 32);
        intensityProgress += 1;
    }
}
