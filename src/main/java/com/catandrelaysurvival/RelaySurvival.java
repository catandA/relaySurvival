package com.catandrelaysurvival;

import com.catandrelaysurvival.screen.CustomDeathScreen;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.catandrelaysurvival.WorldBackupUtil.onNewDay;

public class RelaySurvival implements ModInitializer {
	public static final String MOD_ID = "relaysurvival";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private long lastTickTime = -1;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
		LOGGER.info("Hello Fabric world!");

		ServerTickEvents.END_SERVER_TICK.register(this::checkNewDay);
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			BackupCommand.register(dispatcher);
		});

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof DeathScreen) {
				// 取消原版死亡屏幕，替换为自定义屏幕
				client.setScreen(new CustomDeathScreen(Component.translatable("deathScreen.title.hardcore"), true));
			}
		});
	}

	private void checkNewDay(MinecraftServer server) {
		ServerLevel world = server.overworld();
		long currentTime = world.getGameTime();

		if (lastTickTime == -1) {
			lastTickTime = currentTime;
			return;
		}

		// 检测是否跨越了24000的倍数边界
		long lastDay = lastTickTime / 24000L;
		long currentDay = currentTime / 24000L;

		if (currentDay > lastDay) {
			onNewDay(server, currentDay);
		}

		lastTickTime = currentTime;
	}
}