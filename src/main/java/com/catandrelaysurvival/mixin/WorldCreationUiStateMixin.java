package com.catandrelaysurvival.mixin;

import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldCreationUiState.class)
public class WorldCreationUiStateMixin {

	@Inject(method = "getGameMode", at = @At("RETURN"), cancellable = true)
	private void forceHardcore(CallbackInfoReturnable<WorldCreationUiState.SelectedGameMode> cir) {
		cir.setReturnValue(WorldCreationUiState.SelectedGameMode.HARDCORE);
	}

	@Inject(method = "getDifficulty", at = @At("RETURN"), cancellable = true)
	private void forceHard(CallbackInfoReturnable<Difficulty> cir) {
		cir.setReturnValue(Difficulty.HARD);
	}
}