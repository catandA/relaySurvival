package com.catandrelaysurvival.screen;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public class CustomDeathScreen extends Screen {
	private static final ResourceLocation DRAFT_REPORT_ICON_TEXTURE = ResourceLocation.withDefaultNamespace("icon/draft_report");
	private int ticksSinceDeath;
	private final Component message;
	private final boolean isHardcore;
	private Component scoreText;
	private final List<Button> buttons = Lists.<Button>newArrayList();
	@Nullable
	private Button titleScreenButton;

	public CustomDeathScreen(@Nullable Component message, boolean isHardcore) {
		super(Component.translatable(isHardcore ? "deathScreen.title.hardcore" : "deathScreen.title"));
		this.message = message;
		this.isHardcore = isHardcore;
	}

	@Override
	protected void init() {
		this.ticksSinceDeath = 0;
		this.buttons.clear();
//		Text text = this.isHardcore ? Text.translatable("deathScreen.spectate") : Text.translatable("deathScreen.respawn");
//		this.buttons.add(this.addDrawableChild(ButtonWidget.builder(text, button -> {
//			this.client.player.requestRespawn();
//			button.active = false;
//		}).dimensions(this.width / 2 - 100, this.height / 4 + 72, 200, 20).build()));
		this.titleScreenButton = this.addRenderableWidget(
				Button.builder(
								Component.translatable("deathScreen.titleScreen"),
								button -> this.minecraft.getReportingContext().draftReportHandled(this.minecraft, this, this::onTitleScreenButtonClicked, true)
						)
						.bounds(this.width / 2 - 100, this.height / 4 + 96, 200, 20)
						.build()
		);
		this.buttons.add(this.titleScreenButton);
		this.setButtonsActive(false);
		this.scoreText = Component.translatable("deathScreen.score.value", Component.literal(Integer.toString(this.minecraft.player.getScore())).withStyle(ChatFormatting.YELLOW));
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	private void onTitleScreenButtonClicked() {
		if (this.isHardcore) {
			this.quitLevel();
		} else {
			ConfirmScreen confirmScreen = new CustomDeathScreen.TitleScreenConfirmScreen(confirmed -> {
				if (confirmed) {
					this.quitLevel();
				} else {
					this.minecraft.player.respawn();
					this.minecraft.setScreen(null);
				}
			}, Component.translatable("deathScreen.quit.confirm"), CommonComponents.EMPTY, Component.translatable("deathScreen.titleScreen"), Component.translatable("deathScreen.respawn"));
			this.minecraft.setScreen(confirmScreen);
			confirmScreen.setDelay(20);
		}
	}

	private void quitLevel() {
		if (this.minecraft.level != null) {
			this.minecraft.level.disconnect(ClientLevel.DEFAULT_QUIT_MESSAGE);
		}

		this.minecraft.disconnectWithSavingScreen();
		this.minecraft.setScreen(new TitleScreen());
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
		super.render(context, mouseX, mouseY, deltaTicks);
		context.pose().pushMatrix();
		context.pose().scale(2.0F, 2.0F);
		context.drawCenteredString(this.font, this.title, this.width / 2 / 2, 30, CommonColors.WHITE);
		context.pose().popMatrix();
		if (this.message != null) {
			context.drawCenteredString(this.font, this.message, this.width / 2, 85, CommonColors.WHITE);
		}

		context.drawCenteredString(this.font, this.scoreText, this.width / 2, 100, CommonColors.WHITE);
		if (this.message != null && mouseY > 85 && mouseY < 85 + 9) {
			Style style = this.getTextComponentUnderMouse(mouseX);
			context.renderComponentHoverEffect(this.font, style, mouseX, mouseY);
		}

		if (this.titleScreenButton != null && this.minecraft.getReportingContext().hasDraftReport()) {
			context.blitSprite(
					RenderPipelines.GUI_TEXTURED,
					DRAFT_REPORT_ICON_TEXTURE,
					this.titleScreenButton.getX() + this.titleScreenButton.getWidth() - 17,
					this.titleScreenButton.getY() + 3,
					15,
					15
			);
		}
	}

	@Override
	public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
		fillBackgroundGradient(context, this.width, this.height);
	}

	static void fillBackgroundGradient(GuiGraphics context, int width, int height) {
		context.fillGradient(0, 0, width, height, 1615855616, -1602211792);
	}

	@Nullable
	private Style getTextComponentUnderMouse(int mouseX) {
		if (this.message == null) {
			return null;
		} else {
			int i = this.minecraft.font.width(this.message);
			int j = this.width / 2 - i / 2;
			int k = this.width / 2 + i / 2;
			return mouseX >= j && mouseX <= k ? this.minecraft.font.getSplitter().componentStyleAtWidth(this.message, mouseX - j) : null;
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
		if (this.message != null && click.y() > 85.0 && click.y() < 85 + 9) {
			Style style = this.getTextComponentUnderMouse((int) click.x());
			if (style != null && style.getClickEvent() instanceof ClickEvent.OpenUrl openUrl) {
				return clickUrlAction(this.minecraft, this, openUrl.uri());
			}
		}

		return super.mouseClicked(click, doubled);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean isAllowedInPortal() {
		return true;
	}

	@Override
	public void tick() {
		super.tick();
		this.ticksSinceDeath++;
		if (this.ticksSinceDeath == 20) {
			this.setButtonsActive(true);
		}
	}

	private void setButtonsActive(boolean active) {
		for (Button buttonWidget : this.buttons) {
			buttonWidget.active = active;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class TitleScreenConfirmScreen extends ConfirmScreen {
		public TitleScreenConfirmScreen(BooleanConsumer booleanConsumer, Component text, Component text2, Component text3, Component text4) {
			super(booleanConsumer, text, text2, text3, text4);
		}

		@Override
		public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
			CustomDeathScreen.fillBackgroundGradient(context, this.width, this.height);
		}
	}
}