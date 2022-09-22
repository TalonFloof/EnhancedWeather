package sh.talonfox.enhancedweather.weather;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.BiomeEffectSoundPlayer.MusicLoop;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class Ambience {
    public static MusicLoop DistantWinds = new MusicLoop(new SoundEvent(new Identifier("enhancedweather:ambience.distant_winds")));
    public static MusicLoop CloseWinds = new MusicLoop(new SoundEvent(new Identifier("enhancedweather:ambience.close_winds")));
    public static int CurrentAmbientSound = 0;
    public static boolean HighWindExists = false;
    public static void tick() {
        if(HighWindExists && CurrentAmbientSound != 1 && ClientsideManager.PrecipitationIntensity <= 3) {
            MinecraftClient.getInstance().getSoundManager().play(DistantWinds);
            DistantWinds.fadeIn();
            CurrentAmbientSound = 1;
        } else if(CurrentAmbientSound != 2 && ClientsideManager.PrecipitationIntensity > 3) {
            MinecraftClient.getInstance().getSoundManager().play(CloseWinds);
            CloseWinds.fadeIn();
            CurrentAmbientSound = 2;
        } else {
            if(!HighWindExists && CurrentAmbientSound == 1) {
                DistantWinds.fadeOut();
                CurrentAmbientSound = 0;
            } else if(ClientsideManager.PrecipitationIntensity <= 3 && CurrentAmbientSound == 2) {
                CloseWinds.fadeOut();
                CurrentAmbientSound = 0;
            }
        }
    }
}
