package sh.talonfox.enhancedweather.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class CloudParticle extends SpriteBillboardParticle {
    public float yaw = 0F;
    public float pitch = 0F;

    public CloudParticle(ClientWorld clientWorld, double x, double y, double z, double r, double g, double b, SpriteProvider provider) {
        super(clientWorld, x, y, z, r, g, b);
        this.setSprite(provider);
        this.scale(500F);
        this.collidesWithWorld = false;
        this.setMaxAge(650);
        this.setColor((float)r,(float)g,(float)b);
        this.age = 0;
        this.setAlpha(0.0F);
        this.yaw = Math.round(Math.random()*360);
        this.pitch = -90+Math.round(Math.random()*50)-Math.round(Math.random()*50);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
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
        this.velocityX /= 0.9800000190734863D;
        this.velocityY /= 0.9800000190734863D;
        this.velocityZ /= 0.9800000190734863D;
        if (this.age < 50) {
            this.setAlpha((this.age / 50F));
        } else if(this.age > this.maxAge - 50) {
            this.setAlpha((50 - (this.age - (this.maxAge - 50))) / 50F);
        } else {
            this.setAlpha(1F);
        }
    }

    @Environment(EnvType.CLIENT)
    public record DefaultFactory(SpriteProvider provider) implements ParticleFactory<DefaultParticleType> {
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double r, double g, double b) {
            return new CloudParticle(world, x, y, z, r, g, b, provider);
        }
    }
}
