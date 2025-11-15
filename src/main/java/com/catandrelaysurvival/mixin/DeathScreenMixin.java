package com.catandrelaysurvival.mixin;

import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DeathScreen.class)
public class DeathScreenMixin {

	@Shadow
	private List<ClickableWidget> buttons;

	@Inject(method = "init", at = @At("TAIL"))
	private void removeRespawnButton(CallbackInfo ci) {
		if (this.buttons != null && !this.buttons.isEmpty()) {
			// 移除第一个按钮（重生按钮）
			this.buttons.removeFirst();
		}
	}
}