package com.therandomlabs.randomtweaks.client;

import java.lang.reflect.Field;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public final class SoundSystemReloadHandler {
	public static final Field soundManagerField =
			ReflectionHelper.findField(SoundHandler.class, "sndManager", "field_147694_f");
	public static KeyBinding reloadSoundSystem =
			new KeyBinding("key.reloadSoundSystem", Keyboard.KEY_F8, "key.categories.misc");

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		if(Keyboard.getEventKeyState()) {
			if(reloadSoundSystem.isActiveAndMatches(Keyboard.getEventKey())) {
				final EntityPlayerSP player = Minecraft.getMinecraft().player;

				try {
					reloadSoundSystem();
					player.sendMessage(new TextComponentTranslation("reloadSoundSystem.success"));
				} catch(Exception ex) {
					player.sendMessage(new TextComponentTranslation("reloadSoundSystem.failure.1"));
					player.sendMessage(new TextComponentTranslation("reloadSoundSystem.failure.2",
							ex.getClass().getName(), ex.getMessage()));
					player.sendMessage(new TextComponentTranslation("reloadSoundSystem.failure.3"));
					ex.printStackTrace();
				}
			}
		}
	}

	public void registerKeyBinding() {
		ClientRegistry.registerKeyBinding(reloadSoundSystem);
	}

	public static void reloadSoundSystem() throws Exception {
		((SoundManager) soundManagerField.get(Minecraft.getMinecraft().getSoundHandler())).
				reloadSoundSystem();
	}
}
