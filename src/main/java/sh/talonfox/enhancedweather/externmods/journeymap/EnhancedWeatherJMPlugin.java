package sh.talonfox.enhancedweather.externmods.journeymap;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.ImageOverlay;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.fabric.FabricEvents;
import journeymap.client.api.event.fabric.FullscreenDisplayEvent;
import journeymap.client.api.model.MapImage;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import sh.talonfox.enhancedweather.Enhancedweather;
import sh.talonfox.enhancedweather.client.screens.RadarScreen;
import sh.talonfox.enhancedweather.externmods.ExternalModRegistry;
import sh.talonfox.enhancedweather.weather.weatherevents.Cloud;

public class EnhancedWeatherJMPlugin implements IClientPlugin {
    static IClientAPI JMApi = null;
    static long ticks = 0;
    static boolean showOverlay = true;

    @Override
    public void initialize(IClientAPI jmClientApi) {
        JMApi = jmClientApi;
        Enhancedweather.LOGGER.info("Enhanced Weather by TalonFox, JourneyMap Plugin Initialized!");
        ExternalModRegistry.registerExternalMod("journeymap");
        FabricEvents.ADDON_BUTTON_DISPLAY_EVENT.register(EnhancedWeatherJMPlugin::onButtonDisplay);
    }

    @Override
    public String getModId() {
        return "enhancedweather";
    }

    @Override
    public void onEvent(ClientEvent event) {}

    protected static void onButtonDisplay(FullscreenDisplayEvent.AddonButtonDisplayEvent event) {
        var buttonDisplay = event.getThemeButtonDisplay();
        buttonDisplay.addThemeToggleButton("Enhanced Weather Storm Overlay","storm",showOverlay,button -> {
            button.setToggled(!button.getToggled());
            showOverlay = button.getToggled();
            if(!showOverlay) {
                JMApi.removeAll("enhancedweather");
            }
        });
    }

    public static void clientTickStart(ClientWorld world) {
        ticks++;
        if(ticks % 60 == 0 && showOverlay) {
            if (JMApi == null)
                return;
            JMApi.removeAll("enhancedweather");
            Enhancedweather.CLIENT_WEATHER.Weathers.forEach((id, val) -> {
                if(val instanceof Cloud cloud) {
                    if (!cloud.Placeholder && cloud.Precipitating) {
                        var backImage = new MapImage(cloud.HailIntensity == 2 ? RadarScreen.HIGH_HAIL_INDICATOR : (cloud.HailIntensity == 1 ? RadarScreen.LOW_HAIL_INDICATOR : (cloud.Thundering ? RadarScreen.LIGHTNING_INDICATOR : RadarScreen.RAIN2_INDICATOR)),16,16);
                        backImage.setOpacity(1F);
                        backImage.setDisplayWidth(128);
                        backImage.setDisplayHeight(128);
                        var backOverlay = new ImageOverlay("enhancedweather", "back_overlay_"+id.toString(), new BlockPos(val.Position).add(new Vec3i(-64, 0, -64)), new BlockPos(val.Position).add(new Vec3i(64, 0, 64)),backImage);
                        backOverlay.setDimension(world.getRegistryKey());
                        backOverlay.setDisplayOrder(Integer.MAX_VALUE-1);
                        try {
                            JMApi.show(backOverlay);
                            if(cloud.WindIntensity > 0 || (cloud.Supercell && cloud.TornadoStage < 0)) {
                                var frontImage = new MapImage(cloud.Supercell ? RadarScreen.SUPERCELL_INDICATOR : RadarScreen.WIND_INDICATOR,16,16);
                                frontImage.setOpacity(1F);
                                frontImage.setDisplayWidth(128);
                                frontImage.setDisplayHeight(128);
                                var frontOverlay = new ImageOverlay("enhancedweather", "front_overlay_"+id.toString(), new BlockPos(val.Position).add(new Vec3i(-64, 0, -64)), new BlockPos(val.Position).add(new Vec3i(64, 0, 64)),frontImage);
                                frontOverlay.setDimension(world.getRegistryKey());
                                frontOverlay.setDisplayOrder(Integer.MAX_VALUE);
                                JMApi.show(frontOverlay);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }
    }
}
