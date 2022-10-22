package sh.talonfox.enhancedweather.externmods.journeymap;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;
import sh.talonfox.enhancedweather.Enhancedweather;

public class EnhancedWeatherJMPlugin implements IClientPlugin {

    @Override
    public void initialize(IClientAPI jmClientApi) {
        Enhancedweather.LOGGER.info("Enhanced Weather by TalonFox, JourneyMap Plugin Initialized!");
    }

    @Override
    public String getModId() {
        return "enhancedweather";
    }

    @Override
    public void onEvent(ClientEvent event) {

    }
}
