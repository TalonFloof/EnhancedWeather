package sh.talonfox.enhancedweather.mixin.client;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfox.enhancedweather.Enhancedweather;
import sh.talonfox.enhancedweather.client.screens.OutdatedWeatherFormatScreen;

import java.io.IOException;

@Mixin(WorldListWidget.WorldEntry.class)
public abstract class MixinWorldEntry {
    @Shadow
    @Final
    private LevelSummary level;
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    @Final
    private SelectWorldScreen screen;

    protected void openWorld()  {
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        if (client.getLevelStorage().levelExists(this.level.getName())) {
            client.setScreenAndRender(new MessageScreen(Text.translatable("selectWorld.data_read")));
            client.createIntegratedServerLoader().start(this.screen, this.level.getName());
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/WorldListWidget$WorldEntry;start()V"), method = "play")
    public void outdatedFormatChecker(WorldListWidget.WorldEntry instance) throws SyntaxError, IOException {
        var worldPath = client.runDirectory.toPath().resolve("saves").resolve(level.getName());
        if(worldPath.resolve("enhancedweather").toFile().exists()) {
            var cloudJsonFile = worldPath.resolve("enhancedweather").resolve("Clouds_DIM0.json5").toFile();
            if(cloudJsonFile.exists()) {
                client.setScreen(new OutdatedWeatherFormatScreen(screen,(choice) -> {
                    if(choice) {
                        worldPath.resolve("enhancedweather").resolve("Clouds_DIM0.json5").toFile().delete();
                        openWorld();
                    } else {
                        client.setScreen(screen);
                    }
                }));
            } else {
                cloudJsonFile = worldPath.resolve("enhancedweather").resolve("Weather_DIM0.json5").toFile();
                if(cloudJsonFile.exists()) {
                    JsonObject jsonObject = Jankson.builder().build().load(cloudJsonFile);
                    if(jsonObject.getLong("DataFormat",Enhancedweather.WEATHER_DATA_VERSION) != Enhancedweather.WEATHER_DATA_VERSION) {
                        client.setScreen(new OutdatedWeatherFormatScreen(screen,(choice) -> {
                            if(choice) {
                                worldPath.resolve("enhancedweather").resolve("Weather_DIM0.json5").toFile().delete();
                                openWorld();
                            } else {
                                client.setScreen(screen);
                            }
                        }));
                    } else {
                        openWorld();
                    }
                } else {
                    openWorld();
                }
            }
        } else {
            openWorld();
        }
    }
}
