package sh.talonfloof.enhancedweather.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TornadoParticle extends SpriteBillboardParticle {
    private static long nextID = 0;
    public long ID = 0;
    public float yaw = 0F;
    public float pitch = 0F;
    public boolean velocityDecay = false;
    protected static Random rand = Random.create();

    public TornadoParticle(ClientWorld clientWorld, double x, double y, double z, double r, double g, double b, SpriteProvider provider) {
        super(clientWorld, x, y, z, r, g, b);
        ID = nextID;
        nextID += 1;
        this.setSprite(provider);
        this.setBoundingBoxSpacing(0.25F, 0.25F);
        this.scale = 200F*0.1F;
        this.collidesWithWorld = false;
        this.setMaxAge(300+rand.nextInt(100));
        this.setColor((float)g,(float)g,(float)b);
        this.age = 0;
        this.setAlpha(0.0F);
        this.yaw = Math.round(Math.random()*360);
        this.pitch = -90+Math.round(Math.random()*50)-Math.round(Math.random()*50);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d vec3d = camera.getPos();
        float f = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
        Quaternionf quaternion;
        quaternion = new Quaternionf(0, 0, 0, 1);
        while (yaw >= 180.0F)
        {
            yaw -= 360.0F;
        }
        while (yaw <= -180.0F)
        {
            yaw += 360.0F;
        }
        quaternion.mul(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
        quaternion.mul(RotationAxis.POSITIVE_X.rotationDegrees(pitch));
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
        vertexConsumer.vertex(vector3fs[0].x, vector3fs[0].y, vector3fs[0].z).texture(m, o).color(this.red, this.green, this.blue, this.alpha).normal(0,0,0).light(p).next();
        vertexConsumer.vertex(vector3fs[1].x, vector3fs[1].y, vector3fs[1].z).texture(m, n).color(this.red, this.green, this.blue, this.alpha).normal(0,0,0).light(p).next();
        vertexConsumer.vertex(vector3fs[2].x, vector3fs[2].y, vector3fs[2].z).texture(l, n).color(this.red, this.green, this.blue, this.alpha).normal(0,0,0).light(p).next();
        vertexConsumer.vertex(vector3fs[3].x, vector3fs[3].y, vector3fs[3].z).texture(l, o).color(this.red, this.green, this.blue, this.alpha).normal(0,0,0).light(p).next();
    }

    @Override
    public void tick() {
        super.tick();
        this.velocityX = 0;
        this.velocityZ = 0;
        if(!velocityDecay) {
            this.velocityY /= 0.9800000190734863D;
        }
        if(this.age > this.maxAge - 50) {
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
    public void setX(double x) {
        this.prevPosX = x;
        this.x = x;
    }
    public void setZ(double z) {
        this.prevPosZ = z;
        this.z = z;
    }

    public void addVelocity(double x, double y, double z) {
        this.velocityX += x;
        this.velocityY += y;
        this.velocityZ += z;
    }

    public void setScale(float s) {
        this.scale = s*0.15F;
    }

    public int getAge() {
        return age;
    }

    public record DefaultFactory(SpriteProvider provider) implements ParticleFactory<DefaultParticleType> {
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double r, double g, double b) {
            return new TornadoParticle(world, x, y, z, r, g, b, provider);
        }
    }
}
