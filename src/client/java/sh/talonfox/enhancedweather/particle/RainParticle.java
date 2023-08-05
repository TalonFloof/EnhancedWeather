package sh.talonfox.enhancedweather.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSources;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RainParticle extends SpriteBillboardParticle {
    public RainParticle(ClientWorld clientWorld, double x, double y, double z, double r, double g, double b, SpriteProvider provider) {
        super(clientWorld, x, y, z, r, g, b);
        this.velocityX = 0.0D;
        this.velocityY = -1.0D;
        this.velocityZ = 0.0D;
        this.gravityStrength = 1.0F;
        this.scale = 0.5F;
        this.maxAge = Integer.MAX_VALUE;
        this.setSprite(provider);
        BlockPos pos = new BlockPos((int)x,(int)y,(int)z);
        int color = 0x43d5ee;
        this.setColor((float)ColorHelper.Argb.getRed(color)/255F,(float)ColorHelper.Argb.getGreen(color)/255F,(float)ColorHelper.Argb.getBlue(color)/255F);
    }

    @Override
    public void buildGeometry(VertexConsumer builder, Camera camera, float f) {
        Vec3d vec3 = camera.getPos();
        float x = (float) (MathHelper.lerp(f, this.prevPosX, this.x) - vec3.x);
        float y = (float) (MathHelper.lerp(f, this.prevPosY, this.y) - vec3.y);
        float z = (float) (MathHelper.lerp(f, this.prevPosZ, this.z) - vec3.z);
        Quaternionf quaternion = new Quaternionf(camera.getRotation());
        quaternion.mul(RotationAxis.NEGATIVE_X.rotationDegrees(camera.getPitch()));
        quaternion.mul(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw()));
        quaternion.mul(RotationAxis.POSITIVE_Y.rotation((float) Math.atan2(x, z)));

        Vector3f[] vector3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float k = this.getSize(f);

        for (int l = 0; l < 4; ++l) {
            Vector3f vector3f = vector3fs[l];
            vector3f.rotate(quaternion);
            vector3f.mul(k);
            vector3f.add(x, y, z);
        }

        float l = this.getMinU();
        float vector3f = this.getMaxU();
        float m = this.getMinV();
        float n = this.getMaxV();
        int o = this.getBrightness(f);
        builder.vertex(vector3fs[0].x(), vector3fs[0].y(), vector3fs[0].z()).texture(vector3f, n).color(this.red, this.green, this.blue, this.alpha).light(o).next();
        builder.vertex(vector3fs[1].x(), vector3fs[1].y(), vector3fs[1].z()).texture(vector3f, m).color(this.red, this.green, this.blue, this.alpha).light(o).next();
        builder.vertex(vector3fs[2].x(), vector3fs[2].y(), vector3fs[2].z()).texture(l, m).color(this.red, this.green, this.blue, this.alpha).light(o).next();
        builder.vertex(vector3fs[3].x(), vector3fs[3].y(), vector3fs[3].z()).texture(l, n).color(this.red, this.green, this.blue, this.alpha).light(o).next();
    }

    public void tick() {
        super.tick();
        MinecraftClient client = MinecraftClient.getInstance();
        if (this.onGround || this.world.getBlockState(new BlockPos((int) this.x, (int)this.y, (int)this.z)).blocksMovement() || this.world.getFluidState(new BlockPos((int) this.x, (int) this.y, (int) this.z)).isIn(FluidTags.WATER) || this.world.getFluidState(new BlockPos((int) this.x, (int) this.y, (int) this.z)).isIn(FluidTags.LAVA)) {
            client.particleManager.addParticle(ParticleTypes.RAIN, this.x, this.y, this.z, 0, 0, 0);
            this.markDead();
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public record DefaultFactory(SpriteProvider provider) implements ParticleFactory<DefaultParticleType> {

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double r, double g, double b) {
            return new RainParticle(world, x, y, z, r, g, b, provider);
        }
    }
}
