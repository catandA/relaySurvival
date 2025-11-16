package com.catandrelaysurvival;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

// 注册命令
public class BackupCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("backup")
				.requires(source -> source.hasPermission(3)) // 需要操作员权限
				.executes(context -> {
					MinecraftServer server = context.getSource().getServer();
					boolean success = WorldBackupUtil.backupWorld(server);

					if (success) {
						context.getSource().sendSuccess(() ->
								Component.literal("世界备份完成！"), true);
					} else {
						context.getSource().sendFailure(Component.literal("备份失败！"));
					}
					return 1;
				})
		);
	}
}