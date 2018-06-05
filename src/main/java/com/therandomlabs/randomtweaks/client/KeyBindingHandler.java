package com.therandomlabs.randomtweaks.client;

import java.lang.reflect.Field;
import com.therandomlabs.randomtweaks.common.RTConfig;
import com.therandomlabs.randomtweaks.common.RandomTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public final class KeyBindingHandler {
	public static final Field SOUND_MANAGER =
			ReflectionHelper.findField(SoundHandler.class, "sndManager", "field_147694_f");

	public static final KeyBinding NOCLIP = new KeyBinding("key.noclip",
			KeyConflictContext.IN_GAME, KeyModifier.SHIFT, Keyboard.KEY_F4,
			"key.categories.randomtweaks");
	public static final KeyBinding TOGGLE_FOV_CHANGES = new KeyBinding("key.toggleFoVChanges",
			KeyConflictContext.IN_GAME, KeyModifier.SHIFT, Keyboard.KEY_COMMA,
			"key.categories.randomtweaks");
	public static final KeyBinding RELOAD_SOUND_SYSTEM = new KeyBinding("key.reloadSoundSystem",
			KeyConflictContext.IN_GAME, KeyModifier.SHIFT, Keyboard.KEY_F8,
			"key.categories.randomtweaks");
	public static final KeyBinding TOGGLE_TIME_OF_DAY_OVERLAY = new KeyBinding(
			"key.toggleTimeOfDayOverlay", KeyConflictContext.IN_GAME, KeyModifier.CONTROL,
			Keyboard.KEY_BACKSLASH, "key.categories.randomtweaks");
	public static final KeyBinding TOGGLE_AUTO_JUMP = new KeyBinding("key.toggleAutoJump",
			KeyConflictContext.IN_GAME, Keyboard.KEY_B, "key.categories.randomtweaks");

	public static void registerKeyBindings() {
		register(RTConfig.keybinds.noclip, NOCLIP);
		register(RTConfig.keybinds.toggleFoVChanges, TOGGLE_FOV_CHANGES);
		register(RTConfig.keybinds.reloadSoundSystem, RELOAD_SOUND_SYSTEM);
		register(RTConfig.timeofday.enableKeybind, TOGGLE_TIME_OF_DAY_OVERLAY);
		register(RTConfig.client.stepup, TOGGLE_AUTO_JUMP);
	}

	private static void register(boolean flag, KeyBinding keyBinding) {
		if(flag) {
			ClientRegistry.registerKeyBinding(keyBinding);
		}
	}

	@SubscribeEvent
	public static void onKeyInput(InputEvent.KeyInputEvent event) {
		if(!Keyboard.getEventKeyState()) {
			return;
		}

		final int key = Keyboard.getEventKey();

		if(NOCLIP.isActiveAndMatches(key)) {
			if(RTConfig.keybinds.noclip) {
				toggleNoclip();
			}
		} else if(TOGGLE_FOV_CHANGES.isActiveAndMatches(key)) {
			if(RTConfig.keybinds.toggleFoVChanges) {
				toggleFoVChanges();
			}
		} else if(RELOAD_SOUND_SYSTEM.isActiveAndMatches(key)) {
			if(RTConfig.keybinds.reloadSoundSystem) {
				reloadSoundSystem();
			}
		} else if(TOGGLE_TIME_OF_DAY_OVERLAY.isActiveAndMatches(key)) {
			if(RTConfig.timeofday.enableKeybind) {
				TimeOfDayOverlay.toggle();
			}
		} else if(TOGGLE_AUTO_JUMP.isActiveAndMatches(key)) {
			if(RTConfig.client.stepup) {
				StepupHandler.toggle();
			}
		}
	}

	public static void toggleNoclip() {
		if(RTConfig.keybinds.noclip) {
			final EntityPlayerSP player = Minecraft.getMinecraft().player;
			final String gamemode = player.isCreative() ? "sp" : "c";
			player.sendChatMessage("/gamemode " + gamemode);
		}
	}

	public static void toggleFoVChanges() {
		final RTConfig.Data data = RTConfig.Data.get();
		data.fovChanges = !data.fovChanges;
		RTConfig.Data.save();

		if(RTConfig.keybinds.fovChangesStatusMessage) {
			final EntityPlayerSP player = Minecraft.getMinecraft().player;
			player.sendStatusMessage(new TextComponentTranslation(
					"toggleFoVChanges." + (data.fovChanges ? "enabled" : "disabled")
			), true);
		}
	}

	public static void reloadSoundSystem() {
		if(!RTConfig.keybinds.reloadSoundSystem || !Keyboard.getEventKeyState() ||
				!RELOAD_SOUND_SYSTEM.isActiveAndMatches(Keyboard.getEventKey())) {
			return;
		}

		final EntityPlayerSP player = Minecraft.getMinecraft().player;

		try {
			((SoundManager) SOUND_MANAGER.get(Minecraft.getMinecraft().getSoundHandler())).
					reloadSoundSystem();
			player.sendStatusMessage(new TextComponentTranslation("reloadSoundSystem.success"),
					true);
		} catch(Exception ex) {
			player.sendMessage(new TextComponentTranslation("reloadSoundSystem.failure.1"));
			player.sendMessage(new TextComponentTranslation("reloadSoundSystem.failure.2",
					ex.getClass().getName(), ex.getMessage()));
			player.sendMessage(new TextComponentTranslation("reloadSoundSystem.failure.3"));
			ex.printStackTrace();
		}
	}
}
