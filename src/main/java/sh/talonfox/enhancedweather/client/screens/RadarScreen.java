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
import sh.talonfox.enhancedweather.EnhancedWeather;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

import static com.mojang.blaze3d.platform.GlConst.*;

public class RadarScreen extends Screen {
    protected long ticks = 0;
    protected static final Identifier DOPPLER_RADAR_OVERLAY = new Identifier("enhancedweather","textures/gui/doppler_radar_circle.png");
    public static final Identifier UNKNOWN_INDICATOR = new Identifier("enhancedweather","textures/gui/unknown_symbol.png");
    public static final Identifier RAIN_INDICATOR = new Identifier("enhancedweather","textures/gui/rain_symbol.png");
    public static final Identifier LIGHTNING_INDICATOR = new Identifier("enhancedweather","textures/gui/lightning_symbol.png");
    public static final Identifier LOW_HAIL_INDICATOR = new Identifier("enhancedweather","textures/gui/low_hail_symbol.png");
    public static final Identifier HIGH_HAIL_INDICATOR = new Identifier("enhancedweather","textures/gui/high_hail_symbol.png");
    public static final Identifier SUPERCELL_INDICATOR = new Identifier("enhancedweather","textures/gui/supercell_symbol.png");
    public static final Identifier WIND_INDICATOR = new Identifier("enhancedweather","textures/gui/wind_symbol.png");
    public static final Identifier TORNADO_INDICATOR = new Identifier("enhancedweather","textures/gui/tornado_symbol.png");
    protected static HashMap<UUID, Long> WeatherListTiming = new HashMap<>();
    protected static HashMap<UUID, JsonObject> WeatherListData = new HashMap<>();
    protected static BlockPos Pos = null;
    protected static final double scanSpeed = 200D;
    protected static boolean Accurate = false;
    protected static boolean Range = false;

    public RadarScreen(BlockPos position, boolean hasAccuracy, boolean increasedRange) {
        super(Text.literal("Radar Screen"));
        Pos = position;
        assert MinecraftClient.getInstance().world != null;
        ticks = MinecraftClient.getInstance().world.getTime();
        WeatherListTiming.clear();
        WeatherListData.clear();
        Accurate = hasAccuracy;
        Range = increasedRange;
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
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, DOPPLER_RADAR_OVERLAY);
        RenderSystem.texParameter(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
        RenderSystem.texParameter(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
        drawTexture(matrices,(width/2)-96,(height/2)-96,192,192,0F,0F,64,64,64,64);
        double angle = (((double)ticks % scanSpeed) / scanSpeed) * 360D;
        castLine(width/2,height/2,(width/2)-(int)(Math.sin(Math.toRadians(angle))*96),(height/2)+(int)(Math.cos(Math.toRadians(angle))*96),(x,y) -> {
            fill(matrices,x,y,x+1,y+1,0xFFFFFFFF);
        });
        WeatherListData.forEach((id, data) -> {
            switch (((JsonPrimitive) Objects.requireNonNull(data.get("Identifier"))).asString()) {
                case "enhancedweather:cloud" -> {
                    if(data.getBoolean("Precipitating",false) && !data.getBoolean("Placeholder",false)) {
                        var icon = !Accurate ? UNKNOWN_INDICATOR : (data.getInt("TornadoStage",0) >= 0 ? TORNADO_INDICATOR : data.getInt("HailIntensity",0) == 2 ? HIGH_HAIL_INDICATOR : (data.getInt("HailIntensity",0) == 1 ? LOW_HAIL_INDICATOR : (data.getBoolean("Thundering", false) ? LIGHTNING_INDICATOR : RAIN_INDICATOR)));
                        //⚠
                        var wind_icon = !Accurate ? null : (data.getInt("TornadoStage",0) >= 0 ? null : data.getBoolean("Supercell",false) ? SUPERCELL_INDICATOR : (data.getInt("WindIntensity",0) > 0 ? WIND_INDICATOR : null));
                        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
                        RenderSystem.enableBlend();
                        RenderSystem.texParameter(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
                        RenderSystem.texParameter(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
                        RenderSystem.setShaderTexture(0, icon);
                        var relativeX = (data.getDouble("X",0D)-Pos.getX())/((Range?2048:1024)/192)-8;
                        var relativeZ = (data.getDouble("Z",0D)-Pos.getZ())/((Range?2048:1024)/192)-8;
                        var finalX = (int)(relativeX+(width/2));
                        var finalZ = (int)(relativeZ+(height/2));
                        drawTexture(matrices,finalX,finalZ,16,16,0F,0F,128,128,128,128);
                        if(wind_icon != null) {
                            RenderSystem.setShaderTexture(0, wind_icon);
                            drawTexture(matrices, finalX, finalZ, 16, 16, 0F, 0F, 128, 128, 128, 128);
                        }
                        if(data.getInt("TornadoStage",0) >= 0 && Accurate) {
                            drawCenteredText(matrices,textRenderer,"EF"+data.getInt("TornadoStage", 0),finalX+8,finalZ-8,0xFFFFFF);
                        } else if(data.getInt("WindIntensity",0) > 1 && Accurate) {
                            drawCenteredText(matrices,textRenderer,data.getInt("WindIntensity",0) == 3 ? "⚠⚠" : "⚠",finalX+8,finalZ-8,0xFF0000);
                        }
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
            if(!(new Vec3d(Pos.getX(),200,Pos.getZ()).isInRange(new Vec3d(data.getDouble("X",0D),200D,data.getDouble("Z",0D)),(Range?1024D:512D)))) {
                WeatherListData.remove(id);
                WeatherListTiming.remove(id);
                continue;
            }
        }
        castLine(Pos.getX(),Pos.getZ(),Pos.getX()-(int)(Math.sin(Math.toRadians(angle))*(Range?1024:512)),Pos.getZ()+(int)(Math.cos(Math.toRadians(angle))*(Range?1024:512)),(x,y) -> {
            EnhancedWeather.CLIENT_WEATHER.Weathers.forEach((id, data) -> {
                if(new Vec3d(x,200,y).isInRange(data.Position, 32D)) {
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
                if(new Vec3d(x,200,y).isInRange(new Vec3d(data.getDouble("X",0D),200D,data.getDouble("Z",0D)), 32D)) {
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
