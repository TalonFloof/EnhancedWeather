package sh.talonfox.enhancedweather.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.nio.file.Path;

public class OutdatedWeatherFormatScreen extends Screen {
    BooleanConsumer ScreenConsumer;
    Screen ParentScreen;
    protected long ticks = 0;

    protected static final Identifier OUTDATED_FORMAT_TEX = new Identifier("enhancedweather","textures/gui/outdated_format.png");

    public OutdatedWeatherFormatScreen(Screen parent, BooleanConsumer consumer) {
        super(Text.literal("Outdated Enhanced Weather Format Warning"));
        ScreenConsumer = consumer;
        ParentScreen = parent;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, OUTDATED_FORMAT_TEX);
        drawTexture(matrices,(width/2)-((48*3)/2),5,48*3,48*3,0F,ticks % 60 < 20?0F:(ticks % 60 < 40?48F:96F),48,48,48,144);
        drawCenteredText(matrices,textRenderer,Text.translatable("text.enhancedweather.outdated_warning.header"), width/2, 48*3+15, 0xFFFF0000);
        drawCenteredText(matrices,textRenderer,Text.translatable("text.enhancedweather.outdated_warning.line1"),width/2,48*3+30,0xFFFFFFFF);
        drawCenteredText(matrices,textRenderer,Text.translatable("text.enhancedweather.outdated_warning.line2"),width/2,48*3+30+textRenderer.fontHeight,0xFFFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    public void tick() {
        super.tick();
        ticks += 1;
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