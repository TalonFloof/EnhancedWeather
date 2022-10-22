package sh.talonfox.enhancedweather.externmods;

import java.util.ArrayList;

public class ExternalModRegistry {
    static ArrayList<String> externalMods = new ArrayList<>();

    public static void registerExternalMod(String modID) {
        externalMods.add(modID);
    }

    public static boolean containsExternalMod(String modID) {
        return externalMods.contains(modID);
    }
}
