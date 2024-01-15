package sh.talonfloof.enhancedweather.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import sh.talonfloof.enhancedweather.EnhancedWeatherClient;

import java.util.List;

public class HailParticle extends SpriteBillboardParticle {
    public float prevYaw = 0F;
    public float yaw = 0F;
    public float prevPitch = 0F;
    public float pitch = 0F;
    private boolean hasStopped = false;
    private boolean bounced = false;
    private boolean hasCollidedVerticallyDownwards = false;
    public int lastNonZeroBrightness = 0;
    public HailParticle(ClientWorld clientWorld, double x, double y, double z, double r, double g, double b, SpriteProvider provider) {
        super(clientWorld, x, y, z, r, g, b);
        this.yaw = clientWorld.random.nextInt(360) - 180F;
        this.collidesWithWorld = false;
        this.gravityStrength = 3.5F;
        this.maxAge = 70;
        this.velocityX = 0.0D;
        this.velocityY = -0.5D;
        this.velocityZ = 0.0D;
        this.velocityMultiplier = 1.0F;
        this.setSprite(provider);
    }
    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void move(double dx, double dy, double dz) {
        if (this.hasStopped) {
            return;
        }
        double d = dx;
        double e = dy;
        double f = dz;
        if ((dx != 0.0 || dy != 0.0 || dz != 0.0) && dx * dx + dy * dy + dz * dz < MathHelper.square(100.0)) {
            Vec3d vec3d = Entity.adjustMovementForCollisions(null, (Vec3d)new Vec3d(dx, dy, dz), (Box)this.getBoundingBox(), (World)this.world, List.of());
            dx = vec3d.x;
            dy = vec3d.y;
            dz = vec3d.z;
        }
        if (dx != 0.0 || dy != 0.0 || dz != 0.0) {
            this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
            this.repositionFromBoundingBox();
        }
        if (Math.abs(e) >= (double)1.0E-5f && Math.abs(dy) < (double)1.0E-5f) {
            this.hasStopped = true;
        }
        this.onGround = e != dy && e < 0.0;
        this.hasCollidedVerticallyDownwards = e < y;
        if (d != dx) {
            this.velocityX = 0.0;
        }
        if (f != dz) {
            this.velocityZ = 0.0;
        }
        if (onGround && !bounced) {
            this.velocityY = -this.velocityY * 0.2F;
            this.onGround = false;
            bounced = true;
            world.playSoundAtBlockCenter(new BlockPos((int)this.x,(int)this.y,(int)this.z),SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.WEATHER,0.2F,2F,false);
        }
    }

    @Override
    public void tick() {
        super.tick();
        double speedXZ = Math.sqrt(velocityX * velocityX + velocityZ * velocityZ);
        double spinFastRateAdj = 10F * speedXZ * 10F;
        if(!onGround) {
            this.velocityX = EnhancedWeatherClient.windX / 4F;
            this.velocityZ = EnhancedWeatherClient.windZ / 4F;
        }
        this.prevPitch = this.pitch;
        this.prevYaw = this.yaw;
        if(!onGround) {
            this.pitch += (float) spinFastRateAdj;
            this.yaw += (float) -spinFastRateAdj;
        }
        if(onGround && bounced) {
            if(this.age < this.maxAge-20) {
                this.age = Math.max(this.age, this.maxAge - 20);
            }
            this.alpha = 1F-((this.age-(this.maxAge-20))/20.0F);
        }
    }

    @Override
    public void buildGeometry(VertexConsumer buffer, Camera camera, float tickDelta) {
        Vec3d vec3 = camera.getPos();
        float f = (float)(MathHelper.lerp(tickDelta, this.prevPosX, this.x) - vec3.x);
        float f1 = (float)(MathHelper.lerp(tickDelta, this.prevPosY, this.y) - vec3.y);
        float f2 = (float)(MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - vec3.z);
        Quaternionf quaternion = new Quaternionf(0, 0, 0, 1);
        quaternion.mul(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, this.prevYaw, yaw)));
        quaternion.mul(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.lerp(tickDelta, this.prevPitch, pitch)));

        Vector3f[] avector3f = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)};

        Vector3f[] avector3f2 = new Vector3f[]{
                new Vector3f(0.0F, -1.0F, -1.0F),
                new Vector3f(0.0F, 1.0F, -1.0F),
                new Vector3f(0.0F, 1.0F, 1.0F),
                new Vector3f(0.0F, -1.0F, 1.0F)};

        Vector3f[] avector3f3 = new Vector3f[]{
                new Vector3f(-1.0F, 0.0F, -1.0F),
                new Vector3f(-1.0F, 0.0F, 1.0F),
                new Vector3f(1.0F, 0.0F, 1.0F),
                new Vector3f(1.0F, 0.0F, -1.0F)};

        float f4 = this.getSize(tickDelta);

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternion);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f2[i];
            vector3f.rotate(quaternion);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f3[i];
            vector3f.rotate(quaternion);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }

        float f7 = this.getMinU();
        float f8 = this.getMaxU();
        float f5 = this.getMinV();
        float f6 = this.getMaxV();
        int j = this.getBrightness(tickDelta);
        if (j > 0) {
            lastNonZeroBrightness = j;
        } else {
            j = lastNonZeroBrightness;
        }
        buffer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).texture(f8, f6).color(this.red, this.green, this.blue, this.alpha).light(j).next();
        buffer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).texture(f8, f5).color(this.red, this.green, this.blue, this.alpha).light(j).next();
        buffer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).texture(f7, f5).color(this.red, this.green, this.blue, this.alpha).light(j).next();
        buffer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).texture(f7, f6).color(this.red, this.green, this.blue, this.alpha).light(j).next();

        buffer.vertex(avector3f2[0].x(), avector3f2[0].y(), avector3f2[0].z()).texture(f8, f6).color(this.red, this.green, this.blue, this.alpha).light(j).next();
        buffer.vertex(avector3f2[1].x(), avector3f2[1].y(), avector3f2[1].z()).texture(f8, f5).color(this.red, this.green, this.blue, this.alpha).light(j).next();
        buffer.vertex(avector3f2[2].x(), avector3f2[2].y(), avector3f2[2].z()).texture(f7, f5).color(this.red, this.green, this.blue, this.alpha).light(j).next();
        buffer.vertex(avector3f2[3].x(), avector3f2[3].y(), avector3f2[3].z()).texture(f7, f6).color(this.red, this.green, this.blue, this.alpha).light(j).next();

        buffer.vertex(avector3f3[0].x(), avector3f3[0].y(), avector3f3[0].z()).texture(f8, f6).color(this.red, this.green, this.blue, this.alpha).light(j).next();
        buffer.vertex(avector3f3[1].x(), avector3f3[1].y(), avector3f3[1].z()).texture(f8, f5).color(this.red, this.green, this.blue, this.alpha).light(j).next();
        buffer.vertex(avector3f3[2].x(), avector3f3[2].y(), avector3f3[2].z()).texture(f7, f5).color(this.red, this.green, this.blue, this.alpha).light(j).next();
        buffer.vertex(avector3f3[3].x(), avector3f3[3].y(), avector3f3[3].z()).texture(f7, f6).color(this.red, this.green, this.blue, this.alpha).light(j).next();
    }
    @Environment(EnvType.CLIENT)
    public record DefaultFactory(SpriteProvider provider) implements ParticleFactory<DefaultParticleType> {

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double r, double g, double b) {
            return new HailParticle(world, x, y, z, r, g, b, provider);
        }
    }
}
