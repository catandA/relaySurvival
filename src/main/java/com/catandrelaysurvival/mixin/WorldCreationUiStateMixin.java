package com.catandrelaysurvival.mixin;

import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;

import static com.catandrelaysurvival.WorldBackupUtil.defaultWorldType;

@Mixin(WorldCreationUiState.class)
public class WorldCreationUiStateMixin {
	@Shadow
	private WorldCreationUiState.WorldTypeEntry worldType;
	@Shadow
	private WorldCreationUiState.SelectedGameMode gameMode;

	@ModifyVariable(method = "setGameMode", at = @At("HEAD"), argsOnly = true)
	private WorldCreationUiState.SelectedGameMode forceSetHardcore(WorldCreationUiState.SelectedGameMode gameMode) {
		return WorldCreationUiState.SelectedGameMode.HARDCORE;
	}

	@ModifyVariable(method = "setDifficulty", at = @At("HEAD"), argsOnly = true)
	private Difficulty forceSetHard(Difficulty difficulty) {
		return Difficulty.HARD;
	}

	@ModifyVariable(method = "setGenerateStructures", at = @At("HEAD"), argsOnly = true)
	private boolean forceSetGenerateStructures(boolean bl) {
		return true;
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void getDefaultWorldType(Path path, WorldCreationContext worldCreationContext, Optional optional, OptionalLong optionalLong, CallbackInfo ci) {
		gameMode = WorldCreationUiState.SelectedGameMode.HARDCORE;
		defaultWorldType = worldType;
	}

	@ModifyVariable(method = "setWorldType", at = @At("HEAD"), argsOnly = true)
	private WorldCreationUiState.WorldTypeEntry forceSetDefaultWorldType(WorldCreationUiState.WorldTypeEntry worldTypeEntry) {
		if (defaultWorldType != null) {
			return defaultWorldType;
		}
		return worldTypeEntry;
	}
}