package com.catandrelaysurvival.mixin;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Widget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.gui.screen.world.CreateWorldScreen$MoreTab")
public class CreateWorldScreenMoreTabMixin {
	@Redirect(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/widget/GridWidget$Adder;add(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;"
			)
	)
	private Widget captureAddParameters(GridWidget.Adder instance, Widget widget) {
		ButtonWidget buttonWidget = (ButtonWidget) widget;
		buttonWidget.active = false;
		return instance.add(widget);
	}
}