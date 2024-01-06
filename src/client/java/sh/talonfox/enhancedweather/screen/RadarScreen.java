package sh.talonfox.enhancedweather.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import sh.talonfox.enhancedweather.CloudRenderManager;
import sh.talonfox.enhancedweather.EnhancedWeatherClient;
import sh.talonfox.enhancedweather.api.EnhancedWeatherAPI;

public class RadarScreen extends Screen {
    private BlockPos pos;
    public RadarScreen(BlockPos pos) {
        super(Text.literal("Radar Screen"));
        this.pos = pos;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        MinecraftClient client = MinecraftClient.getInstance();
        int chunkX = Math.floorDiv(pos.getX(),32);
        int chunkZ = Math.floorDiv(pos.getZ(),32);
        int[] rainColors = {
                0x000000,
                0x008C4D,
                0x01A532,
                0x00CF1C,
                0x0FF114,
                0x7FFE21,
                0xFFF20E,
                0xFFD300,
                0xFFAB00,
        };
        int[] hailColors = {
                0x000000,
                0x008C4D,
                0x01A532,
                0x00CF1C,
                0x0FF114,
                0x7FFE21,
                0xDA061D,
                0xDC009E,
                0xFF00FE,
        };
        int[] thunderColors = {
                0xFE7C00,
                0xF93801,
                0xDA061D,
                0xDC009E,
                0xFF00FE
        };
        context.getMatrices().push();
        context.getMatrices().translate(context.getScaledWindowWidth()/2F,context.getScaledWindowHeight()/2F,0);
        for(int x=-32; x < 32;x++) {
            for(int z=-32;z < 32;z++) {
                int finalX = ((chunkX*32)+(x*32)) - MathHelper.floor(CloudRenderManager.cloudX);
                int finalZ = ((chunkZ*32)+(z*32)) - MathHelper.floor(CloudRenderManager.cloudZ);
                int front = Math.round((Math.max(0,EnhancedWeatherAPI.sampleFront(finalX,finalZ,0.1)-0.2F)/0.8F)*8);

                if(chunkX+x == chunkX && chunkZ+z == chunkZ) {
                    context.fill(x * 2, z * 2, (x * 2) + 2, (z * 2) + 2, 0xffffffff);
                } else {
                    if(EnhancedWeatherClient.windSpeed >= 50F) {
                        context.fill(x * 2, z * 2, (x * 2) + 2, (z * 2) + 2, hailColors[front] | 0xff000000);
                    } else if(EnhancedWeatherAPI.sampleThunderstorm(0, x, z, 0.05) > 0.3) {
                        front = Math.round((Math.max(0, EnhancedWeatherAPI.sampleThunderstorm(0, finalX, finalZ, 0.05) - 0.3F) / 0.7F) * 4);
                        context.fill(x * 2, z * 2, (x * 2) + 2, (z * 2) + 2, thunderColors[front] | 0xff000000);
                    } else {
                        context.fill(x * 2, z * 2, (x * 2) + 2, (z * 2) + 2, rainColors[front] | 0xff000000);
                    }
                }
            }
        }
        context.getMatrices().pop();
    }
}
