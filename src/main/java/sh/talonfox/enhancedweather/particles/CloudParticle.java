package sh.talonfox.enhancedweather.particles;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import sh.talonfox.enhancedweather.Enhancedweather;

import java.util.Random;

public class CloudParticle extends SpriteBillboardParticle {
    private static long nextID = 0;
    public long ID = 0;
    public float yaw = 0F;
    public float pitch = 0F;
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
        this.setColor((float)r,(float)g,(float)b);
        this.age = 0;
        this.setAlpha(0.0F);
        this.yaw = Math.round(Math.random()*360);
        this.pitch = -90+Math.round(Math.random()*50)-Math.round(Math.random()*50);
    }

    @Override
    public ParticleTextureSheet getType() {
        if(this.red < 0.5F && this.green < 0.5F && this.blue < 0.5F) {
            return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
        } else {
            return Enhancedweather.CONFIG.Client_TranslucentClouds ? ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT : ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
        }
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
        Quaternion quaternion;
        quaternion = new Quaternion(0, 0, 0, 1);
        quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getDegreesQuaternion(this.yaw));
        quaternion.hamiltonProduct(Vec3f.POSITIVE_X.getDegreesQuaternion(pit));
        Vec3f[] vec3fs = new Vec3f[]{new Vec3f(-1.0F, -1.0F, 0.0F), new Vec3f(-1.0F, 1.0F, 0.0F), new Vec3f(1.0F, 1.0F, 0.0F), new Vec3f(1.0F, -1.0F, 0.0F)};
        float j = this.getSize(tickDelta);

        for(int k = 0; k < 4; ++k) {
            Vec3f vec3f2 = vec3fs[k];
            vec3f2.rotate(quaternion);
            vec3f2.scale(j);
            vec3f2.add(f, g, h);
        }

        float l = this.getMinU();
        float m = this.getMaxU();
        float n = this.getMinV();
        float o = this.getMaxV();
        int p = this.getBrightness(tickDelta);
        vertexConsumer.vertex(vec3fs[0].getX(), vec3fs[0].getY(), vec3fs[0].getZ()).texture(m, o).color(this.red, this.green, this.blue, this.alpha).light(p).next();
        vertexConsumer.vertex(vec3fs[1].getX(), vec3fs[1].getY(), vec3fs[1].getZ()).texture(m, n).color(this.red, this.green, this.blue, this.alpha).light(p).next();
        vertexConsumer.vertex(vec3fs[2].getX(), vec3fs[2].getY(), vec3fs[2].getZ()).texture(l, n).color(this.red, this.green, this.blue, this.alpha).light(p).next();
        vertexConsumer.vertex(vec3fs[3].getX(), vec3fs[3].getY(), vec3fs[3].getZ()).texture(l, o).color(this.red, this.green, this.blue, this.alpha).light(p).next();
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

    @Environment(EnvType.CLIENT)
    public record DefaultFactory(SpriteProvider provider) implements ParticleFactory<DefaultParticleType> {
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double r, double g, double b) {
            return new CloudParticle(world, x, y, z, r, g, b, provider);
        }
    }
}
