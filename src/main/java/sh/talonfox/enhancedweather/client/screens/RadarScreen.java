package sh.talonfox.enhancedweather.client.screens;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import sh.talonfox.enhancedweather.Enhancedweather;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

public class RadarScreen extends Screen {
    protected long ticks = 0;
    protected static final Identifier DOPPLER_RADAR_OVERLAY = new Identifier("enhancedweather","textures/gui/doppler_radar_circle.png");
    protected static final Identifier RAIN_INDICATOR = new Identifier("enhancedweather","textures/gui/rain_symbol.png");
    protected static final Identifier LIGHTNING_INDICATOR = new Identifier("enhancedweather","textures/gui/lightning_symbol.png");
    protected static HashMap<UUID, Long> WeatherListTiming = new HashMap<>();
    protected static HashMap<UUID, JsonObject> WeatherListData = new HashMap<>();
    protected static BlockPos Pos = null;
    protected static double scanSpeed = 600D;

    public RadarScreen(BlockPos position, boolean hasAccuracy, boolean hasSpeed) {
        super(Text.literal("Radar Screen"));
        Pos = position;
        assert MinecraftClient.getInstance().world != null;
        ticks = MinecraftClient.getInstance().world.getTime();
        WeatherListTiming.clear();
        WeatherListData.clear();
        scanSpeed = hasSpeed?200D:600D;
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
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, DOPPLER_RADAR_OVERLAY);
        drawTexture(matrices,(width/2)-96,(height/2)-96,192,192,0F,0F,64,64,64,64);
        double angle = (((double)ticks % scanSpeed) / scanSpeed) * 360D;
        castLine(width/2,height/2,(width/2)-(int)(Math.sin(Math.toRadians(angle))*96),(height/2)+(int)(Math.cos(Math.toRadians(angle))*96),(x,y) -> {
            fill(matrices,x,y,x+1,y+1,0xFFFFFFFF);
        });
        WeatherListData.forEach((id, data) -> {
            switch (((JsonPrimitive) Objects.requireNonNull(data.get("Identifier"))).asString()) {
                case "enhancedweather:cloud" -> {
                    if(data.getBoolean("Precipitating",false) && !data.getBoolean("Placeholder",false)) {
                        var icon = data.getBoolean("Thundering", false) ? LIGHTNING_INDICATOR : RAIN_INDICATOR;
                        RenderSystem.setShader(GameRenderer::getPositionTexShader);
                        RenderSystem.enableBlend();
                        RenderSystem.setShaderTexture(0, icon);
                        var relativeX = (data.getDouble("X",0D)-Pos.getX())/(2048/192)-8;
                        var relativeZ = (data.getDouble("Z",0D)-Pos.getZ())/(2048/192)-8;
                        var finalX = (int)(relativeX+(width/2));
                        var finalZ = (int)(relativeZ+(height/2));
                        drawTexture(matrices,finalX,finalZ,16,16,0F,0F,16,16,16,16);
                    }
                }
                case "enhancedweather:squall_line" -> {

                }
                default -> {

                }
            }
        });
    }

    @Override
    public void tick() {
        super.tick();
        ticks += 1;
        double angle = (((double)ticks % scanSpeed) / scanSpeed) * 360D;
        WeatherListTiming.forEach((id, val) -> {
            if(val < 40L) {
                WeatherListTiming.replace(id, val+1);
            }
        });
        var stormKeys = WeatherListData.keySet().stream().toList();
        for(UUID id : stormKeys) {
            var data = WeatherListData.get(id);
            if(!(new Vec3d(Pos.getX(),200,Pos.getZ()).isInRange(new Vec3d(data.getDouble("X",0D),200D,data.getDouble("Z",0D)),1024D))) {
                WeatherListData.remove(id);
                WeatherListTiming.remove(id);
                continue;
            }
        }
        castLine(Pos.getX(),Pos.getZ(),Pos.getX()-(int)(Math.sin(Math.toRadians(angle))*1024),Pos.getZ()+(int)(Math.cos(Math.toRadians(angle))*1024),(x,y) -> {
            Enhancedweather.CLIENT_WEATHER.Weathers.forEach((id, data) -> {
                if(new Vec3d(x,200,y).isInRange(data.Position,scanSpeed!=600D?32D:10D)) {
                    if(WeatherListTiming.containsKey(id)) {
                        if(WeatherListTiming.get(id) >= 40L) {
                            WeatherListData.replace(id,data.generateSaveDataJson());
                            WeatherListTiming.replace(id,0L);
                        }
                    } else {
                        WeatherListTiming.put(id,0L);
                        WeatherListData.put(id,data.generateSaveDataJson());
                    }
                }
            });
            var keys = WeatherListData.keySet().stream().toList();
            for(UUID id : keys) {
                var data = WeatherListData.get(id);
                if(new Vec3d(x,200,y).isInRange(new Vec3d(data.getDouble("X",0D),200D,data.getDouble("Z",0D)),scanSpeed!=600D?32D:10D)) {
                    if(WeatherListTiming.get(id) >= 40) {
                        WeatherListData.remove(id);
                        WeatherListTiming.remove(id);
                        continue;
                    }
                }
            }
        });
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
