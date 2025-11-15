package com.catandrelaysurvival;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
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
	}

	private void checkNewDay(MinecraftServer server) {
		ServerWorld world = server.getOverworld();
		long currentTime = world.getTime();

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