package sh.talonfloof.enhancedweather;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import sh.talonfloof.enhancedweather.config.EnhancedWeatherConfig;
import sh.talonfloof.enhancedweather.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

import static sh.talonfloof.enhancedweather.EnhancedWeatherClient.windX;
import static sh.talonfloof.enhancedweather.EnhancedWeatherClient.windZ;

public class CloudRenderManager {
    private static final Identifier CLOUD = new Identifier("enhancedweather","textures/cloud/cloud.png");
    private static int RADIUS = 0;
    private static int SIDE = 0;
    private static int CAPACITY = 0;
    private static CloudChunk[] chunks = new CloudChunk[CAPACITY];
    private static Vec2f[] offsets;
    private static long lastTick = 0;
    public static double prevCloudX = 0;
    public static double prevCloudZ = 0;
    public static double cloudX = 0;
    public static double cloudZ = 0;
    private static Vec3d lastCloudColor = new Vec3d(1,1,1);

    private static int getIndex(int x, int y) {
        return (int)MathUtil.wrap(x, SIDE) * SIDE + (int)MathUtil.wrap(y, SIDE);
    }

    public static void forceUpdate() {
        if(RADIUS == EnhancedWeatherConfig.Client_CloudRadius) {
            for (int i = 0; i < chunks.length; i++) {
                chunks[i].forceUpdate();
            }
        }
    }

    public static void render(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double cameraX, double cameraY, double cameraZ) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(RADIUS != EnhancedWeatherConfig.Client_CloudRadius) {
            RADIUS = EnhancedWeatherConfig.Client_CloudRadius;
            SIDE = RADIUS * 2 + 1;
            CAPACITY = SIDE * SIDE;
            chunks = new CloudChunk[CAPACITY];
            for (int i = 0; i < chunks.length; i++) {
                chunks[i] = new CloudChunk();
            }
            List<Vec2f> offset = new ArrayList<>(CAPACITY);
            for (int x = -RADIUS; x <= RADIUS; x++) {
                for (int z = -RADIUS; z <= RADIUS; z++) {
                    offset.add(new Vec2f(x, z));
                }
            }
            offset.sort((v1, v2) -> {
                int d1 = (int)v1.x * (int)v1.x + (int)v1.y * (int)v1.y;
                int d2 = (int)v2.x * (int)v2.x + (int)v2.y * (int)v2.y;
                return Integer.compare(d1, d2);
            });
            offsets = offset.toArray(Vec2f[]::new);
        }
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);
        RenderSystem.setShaderTexture(0,CLOUD);
        BackgroundRenderer.setFogBlack();
        ShaderProgram shaderProgram = RenderSystem.getShader();
        // Cloud Chunk Rendering
        double eX = cameraX;
        double eZ = cameraZ;
        int centerX = MathHelper.floor((cameraX)/32);
        int centerZ = MathHelper.floor((cameraZ)/32);
        int worldXOffset = (int)(cloudX/32);
        int worldZOffset = (int)(cloudZ/32);
        long currentTick = client.world.getTime();
        if(lastTick != currentTick) {
            prevCloudX = cloudX;
            prevCloudZ = cloudZ;
            Vec2f normal = new Vec2f(windX,windZ).normalize();
            cloudX += ((normal.x*Math.min(1.5,EnhancedWeatherClient.windSpeed/25F)) * 0.002) * 32;
            cloudZ += ((normal.y*Math.min(1.5,EnhancedWeatherClient.windSpeed/25F)) * 0.002) * 32;
            lastTick = currentTick;
        }
        eX -= MathHelper.lerp(tickDelta,prevCloudX,cloudX) % 32;
        eZ -= MathHelper.lerp(tickDelta,prevCloudZ,cloudZ) % 32;
        if(RADIUS == EnhancedWeatherConfig.Client_CloudRadius) {
            boolean canUpdate = true;
            Vec3d curColor = client.world.getCloudsColor(tickDelta);
            for (Vec2f offset : offsets) {
                int cx = centerX + (int)offset.x;
                int cz = centerZ + (int)offset.y;
                int movedX = cx - worldXOffset;
                int movedZ = cz - worldZOffset;
                CloudChunk chunk = chunks[getIndex(movedX, movedZ)];
                chunk.setRenderPosition(cx, cz);
                chunk.checkIfNeedUpdate(movedX, movedZ);
                if (canUpdate && chunk.needUpdate()) {
                    chunk.generateChunk(movedX, movedZ);
                    chunk.tessellate();
                    canUpdate = false;
                }
                if (!chunk.needUpdate()) {
                    matrices.push();
                    matrices.translate(chunk.posX - eX, client.world.getDimensionEffects().getCloudsHeight() - cameraY, chunk.posZ - eZ);
                    matrices.scale(2,2,2);
                    chunk.buf.bind();
                    RenderSystem.setShaderColor((float)curColor.x,(float)curColor.y,(float)curColor.z,1.0F);
                    chunk.buf.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shaderProgram);
                    RenderSystem.setShaderColor(1.0F,1.0F,1.0F,1.0F);
                    VertexBuffer.unbind();
                    matrices.pop();
                }
            }
        }
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static void makeCloudBlock(BufferBuilder tessellator, int x, int y, int z, float r, float g, float b, short[] data, int index) {
        if (x == 0 || data[index - 1] == (short)0xf000) {
            tessellator.vertex(x, y, z).normal(-1,0,0).texture(0,0).color(r,g,b,1.0F).next();
            tessellator.vertex(x, y + 1, z).normal(-1,0,0).texture(0,0).color(r,g,b,1.0F).next();
            tessellator.vertex(x, y + 1, z + 1).normal(-1,0,0).texture(0,0).color(r,g,b,1.0F).next();
            tessellator.vertex(x, y, z + 1).normal(-1,0,0).texture(0,0).color(r,g,b,1.0F).next();
        }
        if (x == 15 || data[index + 1] == (short)0xf000) {
            tessellator.vertex(x + 1, y, z).normal(1,0,0).texture(0,0).color(r,g,b,1.0F).next();
            tessellator.vertex(x + 1, y + 1, z).normal(1,0,0).texture(0,0).color(r,g,b,1.0F).next();
            tessellator.vertex(x + 1, y + 1, z + 1).normal(1,0,0).texture(0,0).color(r,g,b,1.0F).next();
            tessellator.vertex(x + 1, y, z + 1).normal(1,0,0).texture(0,0).color(r,g,b,1.0F).next();
        }

        if (y == 0 || data[index - 16] == (short)0xf000) {
            tessellator.vertex(x, y, z).normal(0,-1,0).texture(0,0).color(r,g,b,1.0F).next();
            tessellator.vertex(x + 1, y, z).normal(0,-1,0).texture(0,0).color(r,g,b,1.0F).next();
            tessellator.vertex(x + 1, y, z + 1).normal(0,-1,0).texture(0,0).color(r,g,b,1.0F).next();
            tessellator.vertex(x, y, z + 1).normal(0,-1,0).texture(0,0).color(r,g,b,1.0F).next();
        }
        if (y == 31 || data[index + 16] == (short)0xf000) {
            tessellator.vertex(x, y + 1, z).normal(0,1,0).texture(0,0).color(r,g,b,1.0F).next();
            tessellator.vertex(x + 1, y + 1, z).normal(0,1,0).texture(0,0).color(r,g,b,1.0F).next();
            tessellator.vertex(x + 1, y + 1, z + 1).normal(0,1,0).texture(0,0).color(r,g,b,1.0F).next();
            tessellator.vertex(x, y + 1, z + 1).normal(0,1,0).texture(0,0).color(r,g,b,1.0F).next();
        }

        if (z == 0 || data[index - 512] == (short)0xf000) {
            tessellator.vertex(x, y, z).normal(0,0,-1).texture(0,0).color(r,g,b,1.0F).next();
            tessellator.vertex(x, y + 1, z).normal(0,0,-1).texture(0,0).color(r,g,b,1.0F).next();
            tessellator.vertex(x + 1, y + 1, z).normal(0,0,-1).texture(0,0).color(r,g,b,1.0F).next();
            tessellator.vertex(x + 1, y, z).normal(0,0,-1).texture(0,0).color(r,g,b,1.0F).next();
        }
        if (z == 15 || data[index + 512] == (short)0xf000) {
            tessellator.vertex(x, y, z + 1).normal(0,0,1).texture(0,0).color(r,g,b,1.0F).next();
            tessellator.vertex(x, y + 1, z + 1).normal(0,0,1).texture(0,0).color(r,g,b,1.0F).next();
            tessellator.vertex(x + 1, y + 1, z + 1).normal(0,0,1).texture(0,0).color(r,g,b,1.0F).next();
            tessellator.vertex(x + 1, y, z + 1).normal(0,0,1).texture(0,0).color(r,g,b,1.0F).next();
        }
    }
}
