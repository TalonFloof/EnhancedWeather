package sh.talonfloof.enhancedweather.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import sh.talonfloof.enhancedweather.EnhancedWeather;
import sh.talonfloof.enhancedweather.EnhancedWeatherClient;
import sh.talonfloof.enhancedweather.particle.TornadoParticle;

import java.util.ArrayList;
import java.util.List;

import static sh.talonfloof.enhancedweather.EnhancedWeatherClient.windX;
import static sh.talonfloof.enhancedweather.EnhancedWeatherClient.windZ;

public class TornadoClient extends Tornado {
    public List<Particle> funnelParticles = new ArrayList<Particle>();
    public Random rand = Random.create();
    public TornadoClient(double x, double y, double z) {
        super(x, y, z);
    }
    long ticks = 0;
    @Override
    public void tickClient() {
        Vec2f normal = new Vec2f(windX,windZ).normalize();
        position = position.add(((normal.x*Math.min(1.5, EnhancedWeatherClient.windSpeed/25F)) * 0.002) * 32,0,((normal.y*Math.min(1.5,EnhancedWeatherClient.windSpeed/25F)) * 0.002) * 32);
        ticks++;
        if((ticks % 3) == 0) {
            assert MinecraftClient.getInstance().world != null;
            int currentY = MinecraftClient.getInstance().world.getTopY(Heightmap.Type.MOTION_BLOCKING, (int) position.x, (int) position.z);
            if (currentY == MinecraftClient.getInstance().world.getBottomY())
                currentY = MinecraftClient.getInstance().world.getSeaLevel() + 1;
            int loopSize = 4;
            for (int i = 0; i < loopSize; i++) {
                if (funnelParticles.size() >= 600) {
                    funnelParticles.get(0).markDead();
                    funnelParticles.remove(0);
                }
                if (funnelParticles.size() < 600) {
                    Vec3d tryPos = new Vec3d(position.x + (rand.nextDouble() * 5D) - (rand.nextDouble() * 5D), currentY, position.z + (rand.nextDouble() * 5D) - (rand.nextDouble() * 5D));
                    TornadoParticle newParticle = (TornadoParticle) MinecraftClient.getInstance().particleManager.addParticle(EnhancedWeather.EW_TORNADO, tryPos.getX(), tryPos.getY(), tryPos.getZ(), 1F, 0.3F, 0.3F);
                    assert newParticle != null;
                    newParticle.setMaxAge(200);
                    newParticle.setScale(200);
                    funnelParticles.add(newParticle);
                }
            }
            for (int i = 0; i < funnelParticles.size(); i++) {
                TornadoParticle ent = (TornadoParticle)funnelParticles.get(i);
                if(ent.getY() > 196) {
                    ent.markDead();
                    funnelParticles.remove(i);
                    i -= 1;
                }
            }
        }
        for(Particle particle : funnelParticles) {
            TornadoParticle part = (TornadoParticle)particle;
            double a = position.x - part.getX();
            double b = position.z - part.getZ();
            part.yaw = -(float)Math.toDegrees((Math.atan2(b, a))) - 90;
            part.pitch = 30F;
            spinParticle(part);
        }
    }

    public void spinParticle(TornadoParticle ent) {
        int currentY = MinecraftClient.getInstance().world.getTopY(Heightmap.Type.MOTION_BLOCKING, (int) position.x, (int) position.z);
        if (currentY == MinecraftClient.getInstance().world.getBottomY())
            currentY = MinecraftClient.getInstance().world.getSeaLevel() + 1;
        double d1 = ent.getX() - position.x;
        double d2 = ent.getZ() - position.z;
        Vec2f normalized = new Vec2f((float)d1,(float)d2).normalize();
        float f = (float)Math.toDegrees(Math.atan2(normalized.y, normalized.x));
        f -= 2;
        double curY = (ent.getY()-currentY)/(192-currentY);
        double radius = MathHelper.lerp(curY,5.0,50.0);
        double x = Math.cos(Math.toRadians(f))*radius;
        double z = Math.sin(Math.toRadians(f))*radius;
        ent.setPos(position.x+x,ent.getY(),position.z+z);
        ent.setVelocityY(2);
    }
}
