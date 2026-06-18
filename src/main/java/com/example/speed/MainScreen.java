package com.example.speed;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class MainScreen extends Screen {

    public MainScreen() {
        super(Text.empty());
    }

    @Override
    public void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int buttonWidth = 130;
        int buttonHeight = 30;
        int spacing = 6;

        int x = centerX - buttonWidth / 2;
        int y = centerY - (buttonHeight + spacing) * 2 - 10;

        // Singleplayer
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Singleplayer"), 
                (btn) -> {
                    if (this.client != null) {
                        this.client.setScreen(new SelectWorldScreen(this));
                    }
                })
                .dimensions(x, y, buttonWidth, buttonHeight)
                .build());
        y += buttonHeight + spacing;

        // Multiplayer
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Multiplayer"),
                (btn) -> {
                    if (this.client != null) {
                        this.client.setScreen(new MultiplayerScreen(this));
                    }
                })
                .dimensions(x, y, buttonWidth, buttonHeight)
                .build());
        y += buttonHeight + spacing;

        // Options
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Options"),
                (btn) -> {
                    if (this.client != null) {
                        this.client.setScreen(new OptionsScreen(this, this.client.options));
                    }
                })
                .dimensions(x, y, buttonWidth, buttonHeight)
                .build());
        y += buttonHeight + spacing;

        // Exit
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Exit"),
                (btn) -> {
                    if (this.client != null) {
                        this.client.scheduleStop();
                    }
                })
                .dimensions(x, y, buttonWidth, buttonHeight)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Заливка фона тёмным цветом (можно заменить на изображение)
        this.fillGradient(context, 0, 0, this.width, this.height, 0xFF2C2C2C, 0xFF1A1A1A);
        // Рисуем заголовок (опционально)
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Speed Mod"), this.width / 2, 40, 0xFFFFFFFF);
        // Отрисовка кнопок (автоматически через addDrawableChild)
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false; // Чтобы нельзя было выйти по ESC, если хотите – замените на true
    }
}
