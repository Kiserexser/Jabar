package com.example.speed;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import im.expensive.Expensive;
import im.expensive.utils.client.ClientUtil;
import im.expensive.utils.client.IMinecraft;
import im.expensive.utils.client.Vec2i;
import im.expensive.utils.math.MathUtil;
import im.expensive.utils.render.ColorUtils;
import im.expensive.utils.render.DisplayUtils;
import im.expensive.utils.render.KawaseBlur;
import im.expensive.utils.render.Stencil;
import im.expensive.utils.render.font.Fonts;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class MainScreen extends Screen implements IMinecraft {

    private final Identifier backmenu = new Identifier("expensive", "images/backmenu.png");
    private final List<Button> buttons = new ArrayList<>();

    public MainScreen() {
        super(Text.empty());
    }

    @Override
    public void init(MinecraftClient client, int width, int height) {
        super.init(client, width, height);
        buttons.clear();
        float buttonWidth = 130;
        float buttonHeight = 30;
        float spacing = 6;
        Window window = client.getWindow();
        int w = ClientUtil.calc(window.getScaledWidth());
        int h = ClientUtil.calc(window.getScaledHeight());

        float x = w - buttonWidth - 16;
        float y = h - (buttonHeight + spacing) * 4 - 16;

        buttons.add(new Button(x, y, buttonWidth, buttonHeight, "Singleplayer", () -> client.setScreen(new SelectWorldScreen(this))));
        y += buttonHeight + spacing;
        buttons.add(new Button(x, y, buttonWidth, buttonHeight, "Multiplayer", () -> client.setScreen(new MultiplayerScreen(this))));
        y += buttonHeight + spacing;
        buttons.add(new Button(x, y, buttonWidth, buttonHeight, "Options", () -> client.setScreen(new OptionsScreen(this, client.options))));
        y += buttonHeight + spacing;
        buttons.add(new Button(x, y, buttonWidth, buttonHeight, "Exit", () -> client.scheduleStop()));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Window window = client.getWindow();
        int w = ClientUtil.calc(window.getScaledWidth());
        int h = ClientUtil.calc(window.getScaledHeight());

        DisplayUtils.drawImage(backmenu, 0, 0, w, h, -1);
        KawaseBlur.blur.updateBlur(5.0f, 3);

        for (Button button : buttons) {
            button.render(context, mouseX, mouseY);
        }

        Expensive.getInstance().getAltWidget().render(context, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vec2i fixed = ClientUtil.getMouse((int) mouseX, (int) mouseY);
        buttons.forEach(b -> b.click(fixed.getX(), fixed.getY(), button));
        Expensive.getInstance().getAltWidget().click(fixed.getX(), fixed.getY(), button);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        Expensive.getInstance().getAltWidget().updateScroll((int) mouseX, (int) mouseY, (float) vertical);
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        Expensive.getInstance().getAltWidget().onChar(chr);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Expensive.getInstance().getAltWidget().onKey(keyCode);
        return true;
    }

    @AllArgsConstructor
    private class Button {
        @Getter private final float x, y, width, height;
        private final String text;
        private final Runnable action;

        public void render(DrawContext context, int mouseX, int mouseY) {
            boolean hovered = MathUtil.isHovered(mouseX, mouseY, x, y, width, height);

            Stencil.initStencilToWrite();
            DisplayUtils.drawRoundedRect(x, y, width, height, 5, ColorUtils.rgb(255, 255, 255));
            Stencil.readStencilBuffer(1);
            KawaseBlur.blur.BLURRED.draw();
            Stencil.uninitStencilBuffer();

            DisplayUtils.drawRoundedRect(x, y, width, height, 5, ColorUtils.rgba(0, 0, 0, hovered ? 210 : 180));

            int color = hovered ? ColorUtils.rgb(255, 255, 255) : ColorUtils.rgb(210, 210, 210);
            Fonts.montserrat.drawCenteredText(context.getMatrices(), text, x + width / 2f, y + height / 2f - 4.5f, color, 9f);
        }

        public void click(int mouseX, int mouseY, int button) {
            if (MathUtil.isHovered(mouseX, mouseY, x, y, width, height)) {
                action.run();
            }
        }
    }
}
