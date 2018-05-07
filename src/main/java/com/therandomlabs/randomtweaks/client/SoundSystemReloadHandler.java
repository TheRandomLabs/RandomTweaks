package com.therandomlabs.randomtweaks.client;

import java.lang.reflect.Field;
import com.therandomlabs.randomtweaks.common.RandomTweaks;
import com.therandomlabs.randomtweaks.common.RTConfig;
import com.therandomlabs.randomtweaks.util.Compat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

@EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public final class SoundSystemReloadHandler {
	public static final Field SOUND_MANAGER =
			ReflectionHelper.findField(SoundHandler.class, "sndManager", "field_147694_f");
	public static final KeyBinding RELOAD_SOUND_SYSTEM = new KeyBinding("key.reloadSoundSystem",
			KeyConflictContext.UNIVERSAL, KeyModifier.SHIFT, Keyboard.KEY_F8,
			"key.categories.randomtweaks");

	@SubscribeEvent
	public static void onKeyInput(KeyInputEvent event) {
		if(!RTConfig.keybinds.reloadSoundSystem || !Keyboard.getEventKeyState() ||
				!RELOAD_SOUND_SYSTEM.isActiveAndMatches(Keyboard.getEventKey())) {
			return;
		}

		final EntityPlayerSP player = Minecraft.getMinecraft().player;

		try {
			reloadSoundSystem();
			Compat.sendStatusMessage(player,
					new TextComponentTranslation("reloadSoundSystem.success"));
		} catch(Exception ex) {
			player.sendMessage(new TextComponentTranslation("reloadSoundSystem.failure.1"));
			player.sendMessage(new TextComponentTranslation("reloadSoundSystem.failure.2",
					ex.getClass().getName(), ex.getMessage()));
			player.sendMessage(new TextComponentTranslation("reloadSoundSystem.failure.3"));
			ex.printStackTrace();
		}
	}

	public static void registerKeyBinding() {
		ClientRegistry.registerKeyBinding(RELOAD_SOUND_SYSTEM);
	}

	public static void reloadSoundSystem() throws Exception {
		((SoundManager) SOUND_MANAGER.get(Minecraft.getMinecraft().getSoundHandler())).
				reloadSoundSystem();
	}
}
