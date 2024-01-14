package sh.talonfox.enhancedweather.events;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import sh.talonfox.enhancedweather.EnhancedWeather;
import sh.talonfox.enhancedweather.particle.TornadoParticle;

import java.util.ArrayList;
import java.util.List;

public class TornadoClient extends Tornado {
    public List<Particle> funnelParticles = new ArrayList<Particle>();
    public Random rand = Random.create();
    public TornadoClient(double x, double y, double z) {
        super(x, y, z);
    }
    long ticks = 0;
    public void tickClient() {
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
                    Vec3d tryPos = new Vec3d(position.x + (rand.nextDouble() * 5D) - (rand.nextDouble() * 5D), currentY-32, position.z + (rand.nextDouble() * 5D) - (rand.nextDouble() * 5D));
                    TornadoParticle newParticle = (TornadoParticle) MinecraftClient.getInstance().particleManager.addParticle(EnhancedWeather.EW_TORNADO, tryPos.getX(), tryPos.getY(), tryPos.getZ(), 1F, 0.3F, 0.3F);
                    assert newParticle != null;
                    newParticle.setMaxAge(150 + (500) + rand.nextInt(100));
                    newParticle.setScale(250);
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
            part.yaw = (float)(Math.atan2(b, a) * 180.0D / Math.PI) - 90.0F;
            part.yaw += part.ID % 90;
            part.pitch = -30F;
            spinParticle(part);
        }
    }
    public void spinParticle(TornadoParticle ent) {
        double radius = 10D;
        double scale = 1.5F;
        double d1 = this.position.x - ent.getX();
        double d2 = this.position.z - ent.getZ();
        float f = (float)((Math.atan2(d2, d1) * 180D) / Math.PI) - 90F;
        float f1;

        for (f1 = f; f1 < -180F; f1 += 360F) { }

        for (; f1 >= 180F; f1 -= 360F) { }

        double distY = 196 - ent.getY();
        double distXZ = Math.sqrt(Math.abs(d1)) + Math.sqrt(Math.abs(d2));

        if (ent.getY() - 196 < 0.0D) {
            distY = 1.0D;
        }
        else {
            distY = ent.getY() - 196;
        }
        if (distY > 60) {
            distY = 60;
        }
        double grab = (10D / 1D) * ((Math.abs((50 - distY)) / 50));
        float pullY = 0.0F;
        if (distXZ > 5D)
        {
            grab = grab * (radius / distXZ);
        }
        pullY += 0.05F / (1D / 2F);

        grab += -20;
        double profileAngle = Math.max(1, (75D + grab - (10D * scale)));
        f1 = (float)((double)f1 + profileAngle);

        float f3 = (float)Math.cos(-f1 * 0.01745329F - (float)Math.PI);
        float f4 = (float)Math.sin(-f1 * 0.01745329F - (float)Math.PI);
        float f5 = 0.04F * 1;

        float moveX = f3 * f5;
        float moveZ = f4 * f5;
        float str = 100F;

        pullY *= str / 100F;

        ent.addVelocity(-moveX,pullY,moveZ);
    }
}
