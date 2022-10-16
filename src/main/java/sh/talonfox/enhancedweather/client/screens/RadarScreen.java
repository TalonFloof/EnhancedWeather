package sh.talonfox.enhancedweather.client.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class RadarScreen extends Screen {
    protected long ticks = 0;

    public RadarScreen() {
        super(Text.literal("Radar Screen"));
    }

    protected void drawLine(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        var dx = x2 - x1;
        var dy = y2 - y1;
        var dist = Math.sqrt(Math.pow(dx,2)-Math.pow(dy,2));
        for(int i=0; i < dist; i++) {
            var x = (int)Math.round(MathHelper.lerp(i/dist,x1,x2));
            var y = (int)Math.round(MathHelper.lerp(i/dist,y1,y2));
            fill(matrices,x,y,x,y,color);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

    }

    @Override
    public void tick() {
        super.tick();
        ticks += 1;
    }
}
