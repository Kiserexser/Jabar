package com.example.speed;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeedMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("speedmod");

    @Override
    public void onInitialize() {
        LOGGER.info("Speed Mod is running!");

        // Показываем кастомный экран при запуске (только на клиенте)
        if (MinecraftClient.getInstance() != null) {
            MinecraftClient.getInstance().setScreen(new MainScreen());
        }
    }
}
