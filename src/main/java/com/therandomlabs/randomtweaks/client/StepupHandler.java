package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.config.RTConfig;
import com.therandomlabs.randomtweaks.config.RTData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public final class StepupHandler {
	private enum Mode {
		NO_AUTO_JUMP(VANILLA_STEP_HEIGHT, false, "autoJump.disabled"),
		VANILLA_AUTO_JUMP(VANILLA_STEP_HEIGHT, true, "autoJump.enabled"),
		STEPUP_AUTO_JUMP(STEPUP_STEP_HEIGHT, false, "autoJump.enabledStepup");

		final float stepHeight;
		final boolean enabled;
		final String message;

		Mode(float stepHeight, boolean enabled, String message) {
			this.stepHeight = stepHeight;
			this.enabled = enabled;
			this.message = message;
		}
	}

	public static final float VANILLA_STEP_HEIGHT = 0.6F;
	public static final float STEPUP_STEP_HEIGHT = 1.2F;

	private static final Minecraft mc = Minecraft.getMinecraft();

	private static Potion jumpBoost;
	private static Mode mode;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if(!RTConfig.Client.stepup || mc.player == null) {
			return;
		}

		if(mode == null) {
			final RTData data = RTData.get();
			if(data.stepup) {
				//This will be set to STEPUP_AUTO_JUMP
				mode = Mode.VANILLA_AUTO_JUMP;
				toggle(false);
			} else {
				return;
			}
		}

		if(mc.player.isSneaking()) {
			mc.player.stepHeight = VANILLA_STEP_HEIGHT;
		} else {
			mc.player.stepHeight = mode.stepHeight;

			if(mode == Mode.STEPUP_AUTO_JUMP) {
				if(jumpBoost == null) {
					jumpBoost = Potion.getPotionFromResourceLocation("jump_boost");
				}

				final PotionEffect effect = mc.player.getActivePotionEffect(jumpBoost);

				if(effect != null) {
					mc.player.stepHeight += (effect.getAmplifier() + 1) * 0.75F;
				}
			}
		}
	}

	public static void toggle() {
		if(mc.player != null) {
			toggle(true);
		}
	}

	public static void toggle(boolean sendStatusMessage) {
		final RTData data = RTData.get();
		final boolean autojump =
				mc.gameSettings.getOptionOrdinalValue(GameSettings.Options.AUTO_JUMP);

		if(mode == null) {
			if(data.stepup) {
				mode = Mode.STEPUP_AUTO_JUMP;
			} else {
				mode = autojump ? Mode.VANILLA_AUTO_JUMP : Mode.NO_AUTO_JUMP;
			}
		}

		if(mode == Mode.NO_AUTO_JUMP) {
			mode = Mode.VANILLA_AUTO_JUMP;
		} else if(mode == Mode.VANILLA_AUTO_JUMP) {
			mode = Mode.STEPUP_AUTO_JUMP;
		} else {
			mode = Mode.NO_AUTO_JUMP;
		}

		data.stepup = mode == Mode.STEPUP_AUTO_JUMP;
		RTData.save();

		if(autojump) {
			if(!mode.enabled) {
				//setOptionValue toggles auto jump regardless of value
				mc.gameSettings.setOptionValue(GameSettings.Options.AUTO_JUMP, 0);
			}
		} else {
			if(mode.enabled) {
				//No point in setting it to 1 here - just for clarity
				mc.gameSettings.setOptionValue(GameSettings.Options.AUTO_JUMP, 1);
			}
		}

		if(sendStatusMessage) {
			mc.player.sendStatusMessage(new TextComponentTranslation(mode.message));
		}
	}
}
