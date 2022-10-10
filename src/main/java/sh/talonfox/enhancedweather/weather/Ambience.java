package sh.talonfox.enhancedweather.weather;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.BiomeEffectSoundPlayer.MusicLoop;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionTypes;
import sh.talonfox.enhancedweather.Enhancedweather;

public class Ambience {
    public static MusicLoop DistantWinds = new MusicLoop(new SoundEvent(new Identifier("enhancedweather:ambience.distant_winds")));
    public static MusicLoop CloseWinds = new MusicLoop(new SoundEvent(new Identifier("enhancedweather:ambience.close_winds")));
    public static int CurrentAmbientSound = 0;
    public static boolean HighWindExists = false;
    public static boolean InOverworld = false;
    private static long ticks = 0;
    public static void tick() {
        ticks++;
        if(ticks % 40 == 0) {
            InOverworld = MinecraftClient.getInstance().player.getWorld().getDimensionKey().equals(DimensionTypes.OVERWORLD);
            if(!InOverworld && CurrentAmbientSound != 0) {
                DistantWinds.fadeOut();
                CloseWinds.fadeOut();
                CurrentAmbientSound = 0;
            }
        }
        if(HighWindExists && CurrentAmbientSound != 1 && ClientsideManager.PrecipitationIntensity <= 3 && InOverworld) {
            DistantWinds = new MusicLoop(new SoundEvent(new Identifier("enhancedweather:ambience.distant_winds")));
            MinecraftClient.getInstance().getSoundManager().play(DistantWinds);
            DistantWinds.fadeIn();
            CloseWinds.fadeOut();
            CurrentAmbientSound = 1;
        } else if(CurrentAmbientSound != 2 && ClientsideManager.PrecipitationIntensity > 3 && InOverworld) {
            CloseWinds = new MusicLoop(new SoundEvent(new Identifier("enhancedweather:ambience.close_winds")));
            MinecraftClient.getInstance().getSoundManager().play(CloseWinds);
            DistantWinds.fadeOut();
            CloseWinds.fadeIn();
            CurrentAmbientSound = 2;
        } else {
            if(!HighWindExists && CurrentAmbientSound == 1 && InOverworld) {
                DistantWinds.fadeOut();
                CurrentAmbientSound = 0;
            } else if(ClientsideManager.PrecipitationIntensity <= 3 && CurrentAmbientSound == 2 && InOverworld) {
                CloseWinds.fadeOut();
                CurrentAmbientSound = 0;
            }
        }
    }
}
