package sh.talonfox.enhancedweather;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class EWDebugHud implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        drawContext.drawText(MinecraftClient.getInstance().textRenderer,Float.toString(MinecraftClient.getInstance().world.getSkyAngle(tickDelta)),0,0,0xffffffff,false);
    }
}
