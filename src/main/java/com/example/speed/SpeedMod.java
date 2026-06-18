package com.example.speed;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeedMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("speedmod");

    @Override
    public void onInitialize() {
        LOGGER.info("Speed Mod is running!");

        // Регистрируем событие, которое запустится после полной загрузки клиента
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            // Теперь MinecraftClient существует и готов
            client.setScreen(new MainScreen());
        });
    }
}
