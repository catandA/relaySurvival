package com.catandrelaysurvival.mixin;

import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelInfo.class)
public class LevelInfoMixin {

	@Inject(method = "isHardcore", at = @At("RETURN"), cancellable = true)
	private void forceHardcore(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}

	@Inject(method = "getDifficulty", at = @At("RETURN"), cancellable = true)
	private void forceHard(CallbackInfoReturnable<Difficulty> cir) {
		cir.setReturnValue(Difficulty.HARD);
	}

	@Inject(method = "areCommandsAllowed", at = @At("RETURN"), cancellable = true)
	private void forceNoCommands(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(false);
	}

	@Inject(method = "getGameMode", at = @At("RETURN"), cancellable = true)
	private void forceSurvival(CallbackInfoReturnable<GameMode> cir) {
		cir.setReturnValue(GameMode.SURVIVAL);
	}
}