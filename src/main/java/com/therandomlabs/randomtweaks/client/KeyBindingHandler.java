package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MOD_ID)
public final class KeyBindingHandler {
	public static final KeyBinding TOGGLE_FOV_CHANGES = new KeyBinding(
			"key.toggleFoVChanges",
			KeyConflictContext.IN_GAME,
			KeyModifier.SHIFT,
			Keyboard.KEY_COMMA,
			"key.categories.randomtweaks"
	);

	public static final KeyBinding RELOAD_SOUND_SYSTEM = new KeyBinding(
			"key.reloadSoundSystem",
			KeyConflictContext.IN_GAME,
			KeyModifier.SHIFT,
			Keyboard.KEY_F8,
			"key.categories.randomtweaks"
	);

	public static final KeyBinding TOGGLE_TIME_OF_DAY_OVERLAY = new KeyBinding(
			"key.toggleTimeOfDayOverlay",
			KeyConflictContext.IN_GAME,
			KeyModifier.CONTROL,
			Keyboard.KEY_BACKSLASH,
			"key.categories.randomtweaks"
	);

	public static final KeyBinding TOGGLE_AUTO_JUMP = new KeyBinding(
			"key.toggleAutoJump",
			KeyConflictContext.IN_GAME,
			Keyboard.KEY_B,
			"key.categories.randomtweaks"
	);

	private static final Minecraft mc = Minecraft.getMinecraft();

	public static void registerKeyBindings() {
		register(RTConfig.keybinds.toggleFoVChanges, TOGGLE_FOV_CHANGES);
		register(RTConfig.keybinds.reloadSoundSystem, RELOAD_SOUND_SYSTEM);
		register(
				RTConfig.timeOfDay.enabled && RTConfig.keybinds.toggleTimeOfDayOverlay,
				TOGGLE_TIME_OF_DAY_OVERLAY
		);
		register(RTConfig.client.stepup, TOGGLE_AUTO_JUMP);

		//Attempt to fix https://github.com/TheRandomLabs/RandomTweaks/issues/32

		int index;

		while((index = ArrayUtils.indexOf(mc.gameSettings.keyBindings, null))
				!= ArrayUtils.INDEX_NOT_FOUND) {
			mc.gameSettings.keyBindings = ArrayUtils.remove(mc.gameSettings.keyBindings, index);
		}
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.PostConfigChangedEvent event) {
		if(event.getModID().equals(RandomTweaks.MOD_ID)) {
			registerKeyBindings();
		}
	}

	@SubscribeEvent
	public static void onKeyInput(InputEvent.KeyInputEvent event) {
		if(!Keyboard.getEventKeyState()) {
			return;
		}

		final int key = Keyboard.getEventKey();

		if(TOGGLE_FOV_CHANGES.isActiveAndMatches(key)) {
			if(RTConfig.keybinds.toggleFoVChanges) {
				toggleFoVChanges();
			}
		} else if(RELOAD_SOUND_SYSTEM.isActiveAndMatches(key)) {
			if(RTConfig.keybinds.reloadSoundSystem) {
				reloadSoundSystem();
			}
		} else if(TOGGLE_TIME_OF_DAY_OVERLAY.isActiveAndMatches(key)) {
			if(RTConfig.timeOfDay.enabled && RTConfig.keybinds.toggleTimeOfDayOverlay) {
				TimeOfDayOverlay.toggle();
			}
		} else if(TOGGLE_AUTO_JUMP.isActiveAndMatches(key) && RTConfig.client.stepup) {
			StepupHandler.toggle();
		}
	}

	public static void toggleFoVChanges() {
		final RTConfig.Data data = RTConfig.Data.get();
		data.fovChanges = !data.fovChanges;
		RTConfig.Data.save();

		if(RTConfig.keybinds.fovChangesStatusMessage) {
			mc.player.sendStatusMessage(new TextComponentTranslation(
					"toggleFoVChanges." + (data.fovChanges ? "enabled" : "disabled")
			), true);
		}
	}

	public static void reloadSoundSystem() {
		if(!RTConfig.keybinds.reloadSoundSystem || !Keyboard.getEventKeyState() ||
				!RELOAD_SOUND_SYSTEM.isActiveAndMatches(Keyboard.getEventKey())) {
			return;
		}

		mc.getSoundHandler().sndManager.reloadSoundSystem();
		mc.player.sendStatusMessage(
				new TextComponentTranslation("reloadSoundSystem.success"), true
		);
	}

	private static void register(boolean flag, KeyBinding keyBinding) {
		final GameSettings gameSettings = mc.gameSettings;

		if(flag) {
			if(!ArrayUtils.contains(gameSettings.keyBindings, keyBinding)) {
				gameSettings.keyBindings = ArrayUtils.add(gameSettings.keyBindings, keyBinding);
			}
		} else {
			final int index = ArrayUtils.indexOf(gameSettings.keyBindings, keyBinding);

			if(index != ArrayUtils.INDEX_NOT_FOUND) {
				gameSettings.keyBindings = ArrayUtils.remove(gameSettings.keyBindings, index);
			}
		}
	}
}
