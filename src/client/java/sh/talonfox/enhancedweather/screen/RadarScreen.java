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

import java.util.function.BiConsumer;

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

    protected void castLine(int x1, int y1, int x2, int y2, BiConsumer<Integer, Integer> func) {
        if(Math.abs(y2-y1) < Math.abs(x2-x1)) {
            if(x1 > x2) {
                lineCastLow(x2,y2,x1,y1,func);
            } else {
                lineCastLow(x1,y1,x2,y2,func);
            }
        } else {
            if(y1 > y2) {
                lineCastHigh(x2,y2,x1,y1,func);
            } else {
                lineCastHigh(x1,y1,x2,y2,func);
            }
        }
    }
    protected void lineCastLow(int x1, int y1, int x2, int y2, BiConsumer<Integer, Integer> func) {
        var dx = x2 - x1;
        var dy = y2 - y1;
        var yi = 1;
        if(dy < 0) {
            yi = -1;
            dy = -dy;
        }
        var D = (2 * dy) - dx;
        var y = y1;
        for(int x=x1; x < x2; x++) {
            func.accept(x,y);
            if(D > 0) {
                y += yi;
                D += (2 * (dy - dx));
            } else {
                D += 2*dy;
            }
        }
    }
    protected void lineCastHigh(int x1, int y1, int x2, int y2, BiConsumer<Integer, Integer> func) {
        var dx = x2 - x1;
        var dy = y2 - y1;
        var xi = 1;
        if(dx < 0) {
            xi = -1;
            dx = -dx;
        }
        var D = (2 * dx) - dy;
        var x = x1;
        for(int y=y1; y < y2; y++) {
            func.accept(x,y);
            if(D > 0) {
                x += xi;
                D += (2 * (dx - dy));
            } else {
                D += 2*dx;
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        MinecraftClient client = MinecraftClient.getInstance();
        int chunkX = Math.floorDiv(pos.getX(),64);
        int chunkZ = Math.floorDiv(pos.getZ(),64);
        int[] rainColors = {
                0x1f1f1f,
                0x008C4D,
                0x01A532,
                0x00CF1C,
                0x0FF114,
                0x7FFE21,
                0xFFF20E,
        };
        int[] hailColors = {
                0xFE7C00,
                0xF93801,
                0xDA061D,
                0xDC009E,
                0xFF00FE,
        };
        int[] thunderColors = {
                0xFFD300,
                0xFFAB00,
                0xFE7C00,
                0xF93801,
                0xDA061D,
        };
        context.getMatrices().push();
        context.getMatrices().translate(context.getScaledWindowWidth()/2F,context.getScaledWindowHeight()/2F,0);
        for(int x=-32; x < 32;x++) {
            for(int z=-32;z < 32;z++) {
                if(Math.pow(x,2)+Math.pow(z,2) < Math.pow(32,2)) {
                    int finalX = ((chunkX * 64) + (x * 64)) - MathHelper.floor(CloudRenderManager.cloudX);
                    int finalZ = ((chunkZ * 64) + (z * 64)) - MathHelper.floor(CloudRenderManager.cloudZ);
                    int front = Math.round((Math.max(0, EnhancedWeatherAPI.sampleFront(finalX, finalZ, 0.1) - 0.2F) / 0.8F) * 6);

                    if (chunkX + x == chunkX && chunkZ + z == chunkZ) {
                        context.fill(x * 3, z * 3, (x * 3) + 3, (z * 3) + 3, 0xffffffff);
                    } else {
                        if (EnhancedWeatherAPI.sampleFront(finalX, finalZ, 0.1) >= 0.2F && EnhancedWeatherAPI.sampleThunderstorm(0, finalX, finalZ, 0.05) > 0.3) {
                            front = Math.round((Math.max(0, EnhancedWeatherAPI.sampleThunderstorm(0, finalX, finalZ, 0.05) - 0.3F) / 0.7F) * 4);
                            if (EnhancedWeatherClient.windSpeed >= 50F) {
                                context.fill(x * 3, z * 3, (x * 3) + 3, (z * 3) + 3, hailColors[front] | 0xff000000);
                            } else {
                                context.fill(x * 3, z * 3, (x * 3) + 3, (z * 3) + 3, thunderColors[front] | 0xff000000);
                            }
                        } else {
                            context.fill(x * 3, z * 3, (x * 3) + 3, (z * 3) + 3, rainColors[front] | 0xff000000);
                        }
                    }
                }
            }
        }
        for(int x=-32; x < 32;x++) {
            for (int z = -32; z < 32; z++) {
                double v = Math.pow(x, 2) + Math.pow(z, 2);
                if ((v <= Math.pow(8, 2) && v >= Math.pow(7,2)) || (v <= Math.pow(16, 2) && v >= Math.pow(15,2)) || (v <= Math.pow(24, 2) && v >= Math.pow(23,2))) {
                    context.fill(x * 3, z * 3, (x * 3) + 3, (z * 3) + 3, 0x80ffffff);
                }
            }
        }
        long ticks = client.world.getTime();
        for(int i=0; i < 5; i++) {
            double angle = (((double)((ticks-i)+delta) % 360) / 360) * 360D;
            final float val = i/5F;
            castLine(0, 0, -(int)Math.round(Math.sin(Math.toRadians(angle)) * 32), (int)Math.round(Math.cos(Math.toRadians(angle)) * 32), (x, y) -> {
                context.fill(x * 3, y * 3, (x * 3) + 3, (y * 3) + 3, (MathHelper.lerp(val,255,0) << 24)| 0xFFFFFF);
            });
        }
        context.getMatrices().pop();
    }
}
