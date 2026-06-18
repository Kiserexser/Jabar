package com.example.speed;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SpeedMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("speedmod");

    @Override
    public void onInitialize() {
        LOGGER.info("Asik Client is running!");

        // Запускаем поток, который ждёт появления клиента
        new Thread(() -> {
            MinecraftClient client = null;
            while (client == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
                client = MinecraftClient.getInstance();
            }
            // Когда клиент появился – показываем главное меню
            client.setScreen(new MainScreen());
        }).start();
    }

    // ==================== ГЛАВНОЕ МЕНЮ ====================
    public static class MainScreen extends Screen {
        public MainScreen() {
            super(Text.empty());
        }

        @Override
        public void init() {
            int centerX = this.width / 2;
            int centerY = this.height / 2;
            int buttonWidth = 150;
            int buttonHeight = 30;
            int spacing = 8;

            String[] labels = {"Singleplayer", "Multiplayer", "Options", "Alt Manager", "Exit"};
            Runnable[] actions = {
                    () -> { if (client != null) client.setScreen(new SelectWorldScreen(this)); },
                    () -> { if (client != null) client.setScreen(new MultiplayerScreen(this)); },
                    () -> { if (client != null) client.setScreen(new OptionsScreen(this, client.options)); },
                    () -> { if (client != null) client.setScreen(new AltManagerScreen(this)); },
                    () -> { if (client != null) client.scheduleStop(); }
            };

            int startY = centerY - ((labels.length - 1) * (buttonHeight + spacing)) / 2;
            for (int i = 0; i < labels.length; i++) {
                final int idx = i;
                this.addDrawableChild(ButtonWidget.builder(Text.literal(labels[i]),
                                btn -> actions[idx].run())
                        .dimensions(centerX - buttonWidth / 2,
                                startY + i * (buttonHeight + spacing),
                                buttonWidth, buttonHeight)
                        .build());
            }
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            this.fillGradient(context, 0, 0, this.width, this.height, 0xFF1a1a1a, 0xFF0d0d0d);
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("Asik client"), this.width / 2, 30, 0xFFFFB6C1);
            super.render(context, mouseX, mouseY, delta);
        }

        @Override
        public boolean shouldCloseOnEsc() {
            return false;
        }
    }

    // ==================== АЛЬТ-МЕНЕДЖЕР ====================
    public static class AltManagerScreen extends Screen {
        private final Screen parent;
        private List<String> accounts = new ArrayList<>();
        private int selectedIndex = -1;
        private TextFieldWidget addField;
        private static final String FILE_NAME = ".altmanager";

        public AltManagerScreen(Screen parent) {
            super(Text.literal("Alt Manager"));
            this.parent = parent;
            loadAccounts();
        }

        @Override
        public void init() {
            int centerX = this.width / 2;
            int centerY = this.height / 2;

            addField = new TextFieldWidget(this.textRenderer, centerX - 75, 30, 150, 20,
                    Text.literal("Enter name"));
            addField.setMaxLength(32);
            this.addDrawableChild(addField);

            this.addDrawableChild(ButtonWidget.builder(Text.literal("Add"),
                            btn -> {
                                String name = addField.getText().trim();
                                if (!name.isEmpty() && !accounts.contains(name)) {
                                    accounts.add(name);
                                    saveAccounts();
                                    addField.setText("");
                                    this.clearChildren();
                                    this.init();
                                }
                            })
                    .dimensions(centerX + 80, 30, 50, 20)
                    .build());

            int yPos = 70;
            int spacing = 24;
            for (int i = 0; i < accounts.size(); i++) {
                final int idx = i;
                String display = accounts.get(i);
                this.addDrawableChild(ButtonWidget.builder(Text.literal(display),
                                btn -> {
                                    selectedIndex = idx;
                                    this.clearChildren();
                                    this.init();
                                })
                        .dimensions(centerX - 100, yPos + i * spacing, 200, 20)
                        .build());
            }

            this.addDrawableChild(ButtonWidget.builder(Text.literal("Remove"),
                            btn -> {
                                if (selectedIndex >= 0 && selectedIndex < accounts.size()) {
                                    accounts.remove(selectedIndex);
                                    selectedIndex = -1;
                                    saveAccounts();
                                    this.clearChildren();
                                    this.init();
                                }
                            })
                    .dimensions(centerX + 110, 70, 60, 20)
                    .build());

            this.addDrawableChild(ButtonWidget.builder(Text.literal("Select"),
                            btn -> {
                                if (selectedIndex >= 0 && selectedIndex < accounts.size()) {
                                    String name = accounts.get(selectedIndex);
                                    System.out.println("[Asik] Selected: " + name);
                                }
                            })
                    .dimensions(centerX + 110, 100, 60, 20)
                    .build());

            this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"),
                            btn -> {
                                if (client != null) client.setScreen(parent);
                            })
                    .dimensions(10, this.height - 30, 60, 20)
                    .build());
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            this.fillGradient(context, 0, 0, this.width, this.height, 0xFF1a1a1a, 0xFF0d0d0d);
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("Alt Manager – Asik client"), this.width / 2, 10, 0xFFFFB6C1);

            if (selectedIndex >= 0 && selectedIndex < accounts.size()) {
                int yPos = 70 + selectedIndex * 24;
                int cx = this.width / 2 - 100;
                context.drawBorder(cx - 2, yPos - 2, 204, 24, 0xFFFFB6C1);
            }

            super.render(context, mouseX, mouseY, delta);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (addField.keyPressed(keyCode, scanCode, modifiers)) return true;
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            if (addField.charTyped(chr, modifiers)) return true;
            return super.charTyped(chr, modifiers);
        }

        private void loadAccounts() {
            Path path = Paths.get(FILE_NAME);
            if (Files.exists(path)) {
                try (BufferedReader reader = Files.newBufferedReader(path)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.trim().isEmpty()) accounts.add(line.trim());
                    }
                } catch (IOException ignored) {}
            }
        }

        private void saveAccounts() {
            Path path = Paths.get(FILE_NAME);
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                for (String acc : accounts) {
                    writer.write(acc);
                    writer.newLine();
                }
            } catch (IOException ignored) {}
        }
    }
}
