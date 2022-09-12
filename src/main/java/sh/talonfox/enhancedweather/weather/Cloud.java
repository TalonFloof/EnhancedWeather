package sh.talonfox.enhancedweather.weather;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import sh.talonfox.enhancedweather.Enhancedweather;
import sh.talonfox.enhancedweather.particles.CloudParticle;
import sh.talonfox.enhancedweather.particles.ParticleRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cloud {
    private Manager HostManager;
    public Vec3d Position = null;
    public int Layer = 0;
    public int Water = 0;
    public int Size = 0;
    public boolean Precipitating = false;
    public boolean Placeholder = false;
    public boolean Expanding = false;
    @Environment(EnvType.CLIENT)
    public List<Particle> ParticlesCloud = new ArrayList<Particle>();

    public Cloud(Manager manager, Vec3d pos) {
        HostManager = manager;
        Position = pos;
    }

    public void tickClient() {
        if (Placeholder)
            return;
        Random rand = new Random();
        if ((HostManager.getWorld().getTime() % ((Math.max(1, (int)(100F / Size))) + 2)) == 0) {
            Vec3i playerPos = new Vec3i(MinecraftClient.getInstance().player.getX(), Position.y, MinecraftClient.getInstance().player.getZ());
            Vec3i spawnPos = new Vec3i(Position.x + (Math.random() * Size) - (Math.random() * Size), Position.y, Position.z + (Math.random() * Size) - (Math.random() * Size));
            if (ParticlesCloud.size() < Size && playerPos.getManhattanDistance(spawnPos) < 512) {
                CloudParticle newParticle = (CloudParticle)MinecraftClient.getInstance().particleManager.addParticle(ParticleRegister.CLOUD, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), Precipitating ? 0.2F : 0.7F, 0F, 0F);
                newParticle.setVelocity(-Math.sin(Math.toRadians(Enhancedweather.CLIENT_WIND.AngleGlobal)) * 0.01D,0D,Math.cos(Math.toRadians(Enhancedweather.CLIENT_WIND.AngleGlobal)) * 0.01D);
                ParticlesCloud.add(newParticle);
            }
        }
        for (int i = 0; i < ParticlesCloud.size(); i++) {
            if (!ParticlesCloud.get(i).isAlive()) {
                ParticlesCloud.remove(i);
                i -= 1;
            }
        }
    }
    public void tickServer() {
        if(Layer != 0) {
            Size = 300;
        } else if (Size < 300 && Expanding) {
            Size++;
        }
    }

    public NbtCompound generateUpdate() {
        NbtCompound data = new NbtCompound();
        data.putDouble("X",Position.getX());
        data.putDouble("Y",Position.getY());
        data.putDouble("Z",Position.getZ());
        data.putInt("Layer",Layer);
        data.putInt("Water",Water);
        data.putInt("Size",Size);
        data.putBoolean("Precipitating",Precipitating);
        data.putBoolean("Placeholder",Placeholder);
        data.putBoolean("Expanding",Expanding);
        return data;
    }

    public void applyUpdate(NbtCompound data) {
        Position = new Vec3d(data.getDouble("X"),data.getDouble("Y"),data.getDouble("Z"));
        Layer = data.getInt("Layer");
        Water = data.getInt("Water");
        Size = data.getInt("Size");
        Precipitating = data.getBoolean("Precipitating");
        Placeholder = data.getBoolean("Placeholder");
        Expanding = data.getBoolean("Expanding");
    }
}
