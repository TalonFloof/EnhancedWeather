package sh.talonfox.enhancedweather;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;

public class CloudRenderManager {
    private static final Identifier CLOUD = new Identifier("enhancedweather","textures/cloud.png");
    private static CloudChunk testChunk = new CloudChunk();
    public static void render(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double cameraX, double cameraY, double cameraZ) {
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0,CLOUD);
        matrices.push();
        matrices.translate(-cameraX, 192 - cameraY, -cameraZ);
        ShaderProgram shaderProgram = RenderSystem.getShader();
        // Cloud Chunk Rendering

        matrices.pop();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static void makeFluffyCloudBlock(BufferBuilder tessellator, int x, int y, int z, float r, float g, float b) {
        float px = x;
        float py = y;
        float pz = z;

        tessellator.vertex(px - 0.207107F, py + 1.207107F, pz + 0.5F).texture(0.0F, 0.0F).color(r,g,b,1.0F).next();
        tessellator.vertex(px + 1.207107F, py - 0.207107F, pz + 0.5F).texture(1.0F, 0.0F).color(r,g,b,1.0F).next();
        tessellator.vertex(px + 1.207107F, py - 0.207107F, pz - 1.5F).texture(1.0F, 1.0F).color(r,g,b,1.0F).next();
        tessellator.vertex(px - 0.207107F, py + 1.207107F, pz - 1.5F).texture(0.0F, 1.0F).color(r,g,b,1.0F).next();

        tessellator.vertex(px + 1.207107F, py + 1.207107F, pz + 0.5F).texture(0.0F, 0.0F).color(r,g,b,1.0F).next();
        tessellator.vertex(px - 0.207107F, py - 0.207107F, pz + 0.5F).texture(1.0F, 0.0F).color(r,g,b,1.0F).next();
        tessellator.vertex(px - 0.207107F, py - 0.207107F, pz - 1.5F).texture(1.0F, 1.0F).color(r,g,b,1.0F).next();
        tessellator.vertex(px + 1.207107F, py + 1.207107F, pz - 1.5F).texture(0.0F, 1.0F).color(r,g,b,1.0F).next();

        tessellator.vertex(px + 1.5F, py + 1.207107F, pz + 0.207107F).texture(0.0F, 0.0F).color(r,g,b,1.0F).next();
        tessellator.vertex(px + 1.5F, py - 0.207107F, pz - 1.207107F).texture(1.0F, 0.0F).color(r,g,b,1.0F).next();
        tessellator.vertex(px - 0.5F, py - 0.207107F, pz - 1.207107F).texture(1.0F, 1.0F).color(r,g,b,1.0F).next();
        tessellator.vertex(px - 0.5F, py + 1.207107F, pz + 0.207107F).texture(0.0F, 1.0F).color(r,g,b,1.0F).next();

        tessellator.vertex(px + 1.5F, py + 1.207107F, pz - 1.207107).texture(0.0F, 0.0F).color(r,g,b,1.0F).next();
        tessellator.vertex(px + 1.5F, py - 0.207107F, pz + 0.207107).texture(1.0F, 0.0F).color(r,g,b,1.0F).next();
        tessellator.vertex(px - 0.5F, py - 0.207107F, pz + 0.207107).texture(1.0F, 1.0F).color(r,g,b,1.0F).next();
        tessellator.vertex(px - 0.5F, py + 1.207107F, pz - 1.207107).texture(0.0F, 1.0F).color(r,g,b,1.0F).next();

        tessellator.vertex(px + 1.207107F, py - 0.5F, pz + 0.207107).texture(0.0F, 0.0F).color(r,g,b,1.0F).next();
        tessellator.vertex(px - 0.207107F, py - 0.5F, pz - 1.207107).texture(1.0F, 0.0F).color(r,g,b,1.0F).next();
        tessellator.vertex(px - 0.207107F, py + 1.5F, pz - 1.207107).texture(1.0F, 1.0F).color(r,g,b,1.0F).next();
        tessellator.vertex(px + 1.207107F, py + 1.5F, pz + 0.207107).texture(0.0F, 1.0F).color(r,g,b,1.0F).next();

        tessellator.vertex(px + 1.207107F, py - 0.5F, pz - 1.207107F).texture(0.0F, 0.0F).color(r,g,b,1.0F).next();
        tessellator.vertex(px - 0.207107F, py - 0.5F, pz + 0.207107F).texture(1.0F, 0.0F).color(r,g,b,1.0F).next();
        tessellator.vertex(px - 0.207107F, py + 1.5F, pz + 0.207107F).texture(1.0F, 1.0F).color(r,g,b,1.0F).next();
        tessellator.vertex(px + 1.207107F, py + 1.5F, pz - 1.207107F).texture(0.0F, 1.0F).color(r,g,b,1.0F).next();
    }
}
