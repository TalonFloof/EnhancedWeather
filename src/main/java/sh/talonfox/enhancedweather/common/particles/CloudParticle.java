package sh.talonfox.enhancedweather.common.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import sh.talonfox.enhancedweather.EnhancedWeather;

import java.util.Random;

public class CloudParticle extends SpriteBillboardParticle {
    private static long nextID = 0;
    public long ID = 0;
    public float yaw = 0F;
    public float pitch = 0F;
    public boolean isOpaque;
    public boolean velocityDecay = false;
    protected static Random rand = new Random();

    public CloudParticle(ClientWorld clientWorld, double x, double y, double z, double r, double g, double b, SpriteProvider provider) {
        super(clientWorld, x, y, z, r, g, b);
        ID = nextID;
        nextID += 1;
        this.setSprite(provider);
        this.setBoundingBoxSpacing(0.25F, 0.25F);
        this.scale = 500F*0.15F;
        this.collidesWithWorld = false;
        this.setMaxAge(300+rand.nextInt(100));
        this.setColor((float)g,(float)g,(float)b);
        this.age = 0;
        this.setAlpha(0.0F);
        this.yaw = Math.round(Math.random()*360);
        this.pitch = -90+Math.round(Math.random()*50)-Math.round(Math.random()*50);
        this.isOpaque = (r >= 1 || !EnhancedWeather.CONFIG.Client_TranslucentClouds);
    }

    @Override
    public ParticleTextureSheet getType() {
        return isOpaque ? ParticleTextureSheet.PARTICLE_SHEET_OPAQUE : ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        internalBuildGeometry(vertexConsumer,camera,tickDelta,pitch);
        internalBuildGeometry(vertexConsumer,camera,tickDelta,(pitch+180) % 360);
    }

    public void internalBuildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta, float pit) {
        Vec3d vec3d = camera.getPos();
        float f = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
        Quaternionf quaternion;
        quaternion = new Quaternionf(0, 0, 0, 1);
        quaternion.rotateY((float)Math.toRadians(this.yaw));
        quaternion.rotateX((float)Math.toRadians(pit));
        Vector3f[] vector3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float i = this.getSize(tickDelta);

        for(int j = 0; j < 4; ++j) {
            Vector3f vector3f = vector3fs[j];
            vector3f.rotate(quaternion);
            vector3f.mul(i);
            vector3f.add(f, g, h);
        }

        float l = this.getMinU();
        float m = this.getMaxU();
        float n = this.getMinV();
        float o = this.getMaxV();
        int p = this.getBrightness(tickDelta);
        vertexConsumer.vertex(vector3fs[0].x, vector3fs[0].y, vector3fs[0].z).texture(m, o).color(this.red, this.green, this.blue, this.alpha).light(p).next();
        vertexConsumer.vertex(vector3fs[1].x, vector3fs[1].y, vector3fs[1].z).texture(m, n).color(this.red, this.green, this.blue, this.alpha).light(p).next();
        vertexConsumer.vertex(vector3fs[2].x, vector3fs[2].y, vector3fs[2].z).texture(l, n).color(this.red, this.green, this.blue, this.alpha).light(p).next();
        vertexConsumer.vertex(vector3fs[3].x, vector3fs[3].y, vector3fs[3].z).texture(l, o).color(this.red, this.green, this.blue, this.alpha).light(p).next();
    }

    @Override
    public void tick() {
        super.tick();
        if(!velocityDecay) {
            this.velocityX /= 0.9800000190734863D;
            this.velocityY /= 0.9800000190734863D;
            this.velocityZ /= 0.9800000190734863D;
        }
        if (this.age < 50) {
            this.setAlpha((this.age / 50F));
        } else if(this.age > this.maxAge - 50) {
            this.setAlpha((50 - (this.age - (this.maxAge - 50))) / 50F);
        } else {
            this.setAlpha(1.0F);
        }
    }

    public void setAge(int newAge) {
        this.age = newAge;
    }

    public void setVelocityX(double velX) {
        velocityX = velX;
    }

    public void setVelocityY(double velY) {
        velocityY = velY;
    }

    public void setVelocityZ(double velZ) {
        velocityZ = velZ;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public double getVelocityZ() {
        return velocityZ;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void addVelocity(double x, double y, double z) {
        this.velocityX += x;
        this.velocityY += y;
        this.velocityZ += z;
    }

    public void setScale(float s) {
        this.scale = s*0.15F;
    }

    @Environment(EnvType.CLIENT)
    public record DefaultFactory(SpriteProvider provider) implements ParticleFactory<DefaultParticleType> {
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double r, double g, double b) {
            return new CloudParticle(world, x, y, z, r, g, b, provider);
        }
    }
}
