package com.catandrelaysurvival.mixin;

import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelSettings.class)
public class LevelSettingsMixin {

	@Inject(method = "hardcore", at = @At("RETURN"), cancellable = true)
	private void forceHardcore(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}

	@Inject(method = "difficulty", at = @At("RETURN"), cancellable = true)
	private void forceHard(CallbackInfoReturnable<Difficulty> cir) {
		cir.setReturnValue(Difficulty.HARD);
	}

	@Inject(method = "allowCommands", at = @At("RETURN"), cancellable = true)
	private void forceNoCommands(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(false);
	}

	@Inject(method = "gameType", at = @At("RETURN"), cancellable = true)
	private void forceSurvival(CallbackInfoReturnable<GameType> cir) {
		cir.setReturnValue(GameType.SURVIVAL);
	}
}