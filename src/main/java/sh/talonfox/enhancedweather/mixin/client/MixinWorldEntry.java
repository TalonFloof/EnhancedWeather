package sh.talonfox.enhancedweather.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.talonfox.enhancedweather.client.screens.OutdatedWeatherFormatScreen;

@Mixin(WorldListWidget.WorldEntry.class)
public class MixinWorldEntry {
    @Shadow
    @Final
    private LevelSummary level;
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    @Final
    private SelectWorldScreen screen;

    private static boolean validatedFormat = false;

    @Inject(at = @At(value = "HEAD"), method = "play", cancellable = true)
    public void outdatedFormatChecker(CallbackInfo ci) {
        if(level.isUnavailable()) {
            validatedFormat = true;
            ci.cancel();
        }
        var worldFolderName = Util.replaceInvalidChars(level.getName(), Identifier::isPathCharacterValid);
        var worldPath = client.runDirectory.toPath().resolve("worlds").resolve(worldFolderName);
        if(worldPath.resolve("enhancedweather").toFile().exists()) {
            var cloudJsonFile = worldPath.resolve("enhancedweather").resolve("Clouds_DIM0.json5").toFile();
            if(cloudJsonFile.exists()) {
                client.setScreen(new OutdatedWeatherFormatScreen(screen,(choice) -> {

                }));
            } else {
                cloudJsonFile = worldPath.resolve("enhancedweather").resolve("Weather_DIM0.json5").toFile();
                if(cloudJsonFile.exists()) {

                }
            }
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelSummary;isUnavailable()Z"), method = "play")
    private boolean shouldContinue(LevelSummary instance) {
        var returnValue = instance.isUnavailable() && (!validatedFormat);
        validatedFormat = false;
        return returnValue;
    }
}
