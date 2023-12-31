package sh.talonfox.enhancedweather;

import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import sh.talonfox.enhancedweather.api.EnhancedWeatherAPI;
import sh.talonfox.enhancedweather.util.FastNoiseLite;

public class CloudChunk {
    private static final float[] RAIN_COLOR = new float[] { 66F / 255F, 74F / 255F, 74F / 255F };
    private static final float[] DARK_COLOR = new float[] { 150F / 255F, 176F / 255F, 211F / 255F };
    private static final Random RANDOM = Random.create();
    private static final PerlinNoiseSampler NOISE = new PerlinNoiseSampler(Random.create());

    private boolean needUpdate = true;
    private short[] data;
    public VertexBuffer buf = new VertexBuffer(VertexBuffer.Usage.STATIC);
    private int chunkX = Integer.MIN_VALUE;
    private int chunkZ = Integer.MIN_VALUE;
    public int posX;
    public int posZ;
    public CloudChunk() {
        data = new short[8192];
        for(int i=0; i < 8192; i++) {
            data[i] = (short)0xF000;
        }
    }
    public void setRenderPosition(int chunkX, int chunkZ) {
        this.posX = chunkX << 5;
        this.posZ = chunkZ << 5;
    }
    public void setValue(int x, int y, int z, short val) {
        data[(x & 0xF) | ((y & 0x1F) << 4) | ((z & 0xF) << 9)] = val;
    }

    public void checkIfNeedUpdate(int x, int z) {
        needUpdate = chunkX != x || chunkZ != z;
    }
    public boolean needUpdate() {
        return needUpdate;
    }
    public void forceUpdate() {
        this.chunkX = Integer.MIN_VALUE;
        this.chunkZ = Integer.MIN_VALUE;
        needUpdate = true;
    }

    public void generateChunk(int oX, int oZ) {
        this.chunkX = oX;
        this.chunkZ = oZ;

        for(int i=0; i < 8192; i++) {
            byte x = (byte) (i & 15);
            byte y = (byte) ((i >> 4) & 31);
            byte z = (byte) (i >> 9);
            float rainFront = EnhancedWeatherAPI.sampleFront((oX*16)+x, (oZ*16)+z, 0.2);
            float density = EnhancedWeatherAPI.getCloudDensity(((oX*16)+x) << 1, y << 1, ((oZ*16)+z) << 1, rainFront);
            float coverage = EnhancedWeatherAPI.getCoverage(rainFront);
            if (density < coverage) {
                data[i] = (short)0xF000;
            } else {
                data[i] = (short) ((byte) (rainFront * 15) << 4);
                byte thunder = (byte)(EnhancedWeatherAPI.sampleThunderstorm(0, x, z, 0.1) * rainFront * 15);
                data[i] |= (short) (thunder << 8);
            }
        }
        for(int i=0; i < 8192; i++) {
            if (data[i] == (short)0xf000) continue;
            byte x = (byte) (i & 15);
            byte y = (byte) ((i >> 4) & 31);
            byte z = (byte) (i >> 9);
            byte light = 15;
            for (byte j = 1; j < 15; j++) {
                if (y + j > 31) break;
                int index2 = i + (j << 4);
                if (data[index2] != (short)0xf000) light--;
            }

            if (light > 0) {
                light = (byte) (light - NOISE.sample((float) (x * 0.3), (float)(y * 0.3), (float)(z * 0.3)));
            }
            data[i] |= light;
        }
    }

    public void tessellate() {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        for(int i=0; i < 8192; i++) {
            if(data[i] == (short)0xF000) continue;
            byte x = (byte) (i & 15);
            byte y = (byte) ((i >> 4) & 31);
            byte z = (byte) (i >> 9);
            boolean canDraw = x == 0 || x == 15 || y == 0 || y == 31 || z == 0 || z == 15;
            if (!canDraw) {
                canDraw = data[i + 1] == (short)0xF000 || data[i - 1] == (short)0xF000 ||
                        data[i + 16] == (short)0xF000 || data[i - 16] == (short)0xF000 ||
                        data[i + 512] == (short)0xF000 || data[i - 512] == (short)0xF000;
            }
            if (!canDraw) continue;
            RANDOM.setSeed(MathHelper.hashCode(x,y,z));
            float deltaBrightness = ((data[i] & 15) + RANDOM.nextFloat()) / 15F;
            float deltaWetness = (((data[i] >> 4) & 15) + RANDOM.nextFloat()) / 15F;
            float deltaThunder = ((data[i] >> 8) & 15) / 15F;
            deltaBrightness *= (1 - deltaWetness) * 0.5F + 0.5F;
            deltaThunder = MathHelper.lerp(deltaThunder, 1F, 0.5F);
            float r = MathHelper.lerp(deltaWetness, RAIN_COLOR[0], DARK_COLOR[0]);
            float g = MathHelper.lerp(deltaWetness, RAIN_COLOR[1], DARK_COLOR[1]);
            float b = MathHelper.lerp(deltaWetness, RAIN_COLOR[2], DARK_COLOR[2]);
            r = MathHelper.clamp(MathHelper.lerp(deltaBrightness, r, 1F) * deltaThunder,0,1);
            g = MathHelper.clamp(MathHelper.lerp(deltaBrightness, g, 1F) * deltaThunder,0,1);
            b = MathHelper.clamp(MathHelper.lerp(deltaBrightness, b, 1F) * deltaThunder,0,1);
            CloudRenderManager.makeCloudBlock(bufferBuilder,x,y,z,r,g,b,data,i);
        }
        buf.bind();
        buf.upload(bufferBuilder.end());
        VertexBuffer.unbind();
    }
}
