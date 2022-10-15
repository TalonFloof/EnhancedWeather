package sh.talonfox.enhancedweather.client.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.nio.file.Path;

public class OutdatedWeatherFormatScreen extends Screen {
    BooleanConsumer ScreenConsumer;
    Screen ParentScreen;
    public OutdatedWeatherFormatScreen(Screen parent, BooleanConsumer consumer) {
        super(Text.literal("Outdated Enhanced Weather Format Warning"));
        ScreenConsumer = consumer;
        ParentScreen = parent;
    }

    protected void init() {
        super.init();
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height-25, 150, 20, ScreenTexts.PROCEED, (button) -> {
            ScreenConsumer.accept(true);
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, this.height-25, 150, 20, ScreenTexts.CANCEL, (button) -> {
            ScreenConsumer.accept(false);
        }));
    }
}