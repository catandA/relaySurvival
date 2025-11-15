package com.catandrelaysurvival;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

// 注册命令
public class BackupCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("backup")
            .requires(source -> source.hasPermissionLevel(3)) // 需要操作员权限
            .executes(context -> {
                MinecraftServer server = context.getSource().getServer();
                boolean success = WorldBackupUtil.backupWorld(server);
                
                if (success) {
                    context.getSource().sendFeedback(() -> 
                        Text.literal("世界备份完成！"), true);
                } else {
                    context.getSource().sendError(Text.literal("备份失败！"));
                }
                return 1;
            })
        );
    }
}