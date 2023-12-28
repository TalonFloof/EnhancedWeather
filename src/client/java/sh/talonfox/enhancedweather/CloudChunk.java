package sh.talonfox.enhancedweather;

import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class CloudChunk {
    private static final float[] RAIN_COLOR = new float[] { 66F / 255F, 74F / 255F, 74F / 255F };
    private static final float[] DARK_COLOR = new float[] { 150F / 255F, 176F / 255F, 211F / 255F };
    private static final Random RANDOM = Random.create();

    private short[] data;
    public VertexBuffer buf = new VertexBuffer(VertexBuffer.Usage.STATIC);
    public CloudChunk() {
        data = new short[8192];
        for(int i=0; i < 8192; i++) {
            data[i] = (short)0xF000;
        }
    }
    public void setValue(int x, int y, int z, short val) {
        data[(x & 0xF) | ((y & 0x1F) << 4) | ((z & 0xF) << 9)] = val;
    }

    public void generateChunk() {

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
            r = MathHelper.lerp(deltaBrightness, r, 1F) * deltaThunder;
            g = MathHelper.lerp(deltaBrightness, g, 1F) * deltaThunder;
            b = MathHelper.lerp(deltaBrightness, b, 1F) * deltaThunder;
            CloudRenderManager.makeFluffyCloudBlock(bufferBuilder,x,y,z,r,g,b);
        }
        buf.bind();
        buf.upload(bufferBuilder.end());
        VertexBuffer.unbind();
    }
}
