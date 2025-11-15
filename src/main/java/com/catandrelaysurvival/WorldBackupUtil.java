package com.catandrelaysurvival;

import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.path.SymlinkValidationException;
import net.minecraft.world.level.storage.LevelStorage;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class WorldBackupUtil {
	public static void onNewDay(MinecraftServer server, long currentDay) {
		// 聊天发送消息
		server.getPlayerManager().broadcast(Text.literal("存档在你手里又活过了新的一天！现在的总天数: " + currentDay), false);

		// 创建备份文件夹
		File backupsDir = new File(server.getSavePath(WorldSavePath.ROOT).toFile().getParent(), "backups");
		if (!backupsDir.exists()) {
			backupsDir.mkdirs();
		}

		// 生成备份文件名
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
		String zipFileName = "RelaySurvivalDay" + currentDay + "_" + timeStamp + ".zip";
		backupWorld(server, zipFileName);
	}

	public static boolean backupWorld(MinecraftServer server) {
		return backupWorld(server, null);
	}

	public static boolean backupWorld(MinecraftServer server, String backupName) {
		try {
			File worldDir = server.getSavePath(WorldSavePath.ROOT).toFile();
			File backupsDir = new File(worldDir, "backups");

			if (!backupsDir.exists()) {
				backupsDir.mkdirs();
			}

			String zipFileName;
			if (backupName == null || backupName.isEmpty()) {
				// 生成备份文件名
				String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
				zipFileName = "world_backup_" + timeStamp + ".zip";
			} else {
				zipFileName = backupName + ".zip";
			}
			File zipFile = new File(backupsDir, zipFileName);

			// 创建ZIP备份
			createZipBackup(worldDir, zipFile);

			System.out.println("世界备份完成: " + zipFile.getAbsolutePath());
			return true;

		} catch (IOException e) {
			System.err.println("备份失败: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	private static void createZipBackup(File worldDir, File zipFile) throws IOException {
		// 黑名单：不需要备份的文件和文件夹
		Set<String> blacklist = new HashSet<>(Arrays.asList(
				"session.lock",  // Minecraft会话锁文件
				"backups",       // 备份文件夹（避免递归备份）
				"cache",         // 缓存文件
				"logs",          // 日志文件
				"crash-reports", // 崩溃报告
				"debug",         // 调试文件
				"temp",          // 临时文件
				"tmp"            // 临时文件
		));

		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
			addWorldContentsToZip(zos, worldDir, "", blacklist);
		}
	}

	private static void addWorldContentsToZip(ZipOutputStream zos, File baseDir, String relativePath, Set<String> blacklist) throws IOException {
		File[] files = baseDir.listFiles();
		if (files == null) return;

		for (File file : files) {
			String fileName = file.getName();
			String currentRelativePath = relativePath.isEmpty() ? fileName : relativePath + "/" + fileName;

			// 检查是否在黑名单中
			if (blacklist.contains(fileName.toLowerCase())) {
				continue;
			}

			if (file.isDirectory()) {
				// 递归处理子目录
				addWorldContentsToZip(zos, file, currentRelativePath, blacklist);
			} else {
				// 添加文件到zip
				addFileToZip(zos, file, currentRelativePath);
			}
		}
	}

	private static void addFileToZip(ZipOutputStream zos, File file, String entryName) throws IOException {
		ZipEntry entry = new ZipEntry(entryName);
		zos.putNextEntry(entry);

		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, length);
			}
		}
		zos.closeEntry();
	}
}