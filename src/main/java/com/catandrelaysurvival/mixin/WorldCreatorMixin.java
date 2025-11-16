package com.catandrelaysurvival.mixin;

import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldCreator.class)
public class WorldCreatorMixin {

	@Inject(method = "getGameMode", at = @At("RETURN"), cancellable = true)
	private void forceHardcore(CallbackInfoReturnable<WorldCreator.Mode> cir) {
		cir.setReturnValue(WorldCreator.Mode.HARDCORE);
	}

	@Inject(method = "getDifficulty", at = @At("RETURN"), cancellable = true)
	private void forceHard(CallbackInfoReturnable<Difficulty> cir) {
		cir.setReturnValue(Difficulty.HARD);
	}
}