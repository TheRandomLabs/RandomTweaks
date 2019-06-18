package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.config.RTConfig;
import com.therandomlabs.randomtweaks.config.RTData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
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
			Keyboard.KEY_NONE,
			"key.categories.randomtweaks"
	);

	public static final KeyBinding TOGGLE_AUTO_JUMP = new KeyBinding(
			"key.toggleAutoJump",
			KeyConflictContext.IN_GAME,
			Keyboard.KEY_B,
			"key.categories.randomtweaks"
	);

	private static final Minecraft mc = Minecraft.getMinecraft();

	private static boolean categoryEnabled;

	public static void registerKeyBindings() {
		categoryEnabled = false;

		register(RTConfig.Keybinds.toggleFoVChanges, TOGGLE_FOV_CHANGES);
		register(RTConfig.Keybinds.reloadSoundSystem, RELOAD_SOUND_SYSTEM);
		register(
				RTConfig.TimeOfDay.enabled && RTConfig.Keybinds.toggleTimeOfDayOverlay,
				TOGGLE_TIME_OF_DAY_OVERLAY
		);
		register(RTConfig.Client.stepup, TOGGLE_AUTO_JUMP);

		//Forge just isn't designed to allow keybinds to be toggled in-game
		if(categoryEnabled) {
			KeyBinding.getKeybinds().add("key.categories.randomtweaks");
		} else {
			//If none of the keybinds are enabled, then GuiKeyBindingList.getListEntry
			//returns null for one of the indexes, which causes a NullPointerException
			KeyBinding.getKeybinds().remove("key.categories.randomtweaks");
		}
	}

	@SubscribeEvent
	public static void onKeyInput(InputEvent.KeyInputEvent event) {
		if(!Keyboard.getEventKeyState()) {
			return;
		}

		final int key = Keyboard.getEventKey();

		if(TOGGLE_FOV_CHANGES.isActiveAndMatches(key)) {
			if(RTConfig.Keybinds.toggleFoVChanges) {
				toggleFoVChanges();
			}
		} else if(RELOAD_SOUND_SYSTEM.isActiveAndMatches(key)) {
			if(RTConfig.Keybinds.reloadSoundSystem) {
				reloadSoundSystem();
			}
		} else if(TOGGLE_TIME_OF_DAY_OVERLAY.isActiveAndMatches(key)) {
			if(RTConfig.TimeOfDay.enabled && RTConfig.Keybinds.toggleTimeOfDayOverlay) {
				TimeOfDayOverlay.toggle();
			}
		} else if(TOGGLE_AUTO_JUMP.isActiveAndMatches(key) && RTConfig.Client.stepup) {
			StepupHandler.toggle();
		}
	}

	public static void toggleFoVChanges() {
		final RTData data = RTData.get();
		data.fovChanges = !data.fovChanges;
		RTData.save();

		if(RTConfig.Keybinds.fovChangesStatusMessage) {
			mc.player.sendStatusMessage(new TextComponentTranslation(
					"toggleFoVChanges." + (data.fovChanges ? "enabled" : "disabled")
			), true);
		}
	}

	public static void reloadSoundSystem() {
		if(!RTConfig.Keybinds.reloadSoundSystem || !Keyboard.getEventKeyState() ||
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

			categoryEnabled = true;
		} else {
			final int index = ArrayUtils.indexOf(gameSettings.keyBindings, keyBinding);

			if(index != ArrayUtils.INDEX_NOT_FOUND) {
				gameSettings.keyBindings = ArrayUtils.remove(gameSettings.keyBindings, index);
			}
		}
	}
}
