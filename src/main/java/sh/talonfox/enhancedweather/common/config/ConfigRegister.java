package sh.talonfox.enhancedweather.common.config;

import draylar.omegaconfiggui.OmegaConfigGui;
import sh.talonfox.enhancedweather.Enhancedweather;

public class ConfigRegister {
    public static void Initialize() {
        OmegaConfigGui.registerConfigScreen(Enhancedweather.CONFIG);
    }
}
