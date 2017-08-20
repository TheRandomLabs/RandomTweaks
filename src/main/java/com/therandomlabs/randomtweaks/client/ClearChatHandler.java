package com.therandomlabs.randomtweaks.client;

import org.lwjgl.input.Keyboard;
import com.therandomlabs.randomtweaks.common.RandomTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public class ClearChatHandler {
	public static final KeyBinding CLEAR_CHAT =
			new KeyBinding("key.clearChat", Keyboard.KEY_I, "key.categories.randomtweaks");

	@SubscribeEvent
	public static void onKeyInput(KeyInputEvent event) {
		if(Keyboard.getEventKeyState() && CLEAR_CHAT.isActiveAndMatches(Keyboard.getEventKey()) &&
				Minecraft.getMinecraft().ingameGUI != null) {
			Minecraft.getMinecraft().ingameGUI.getChatGUI().clearChatMessages(false);
		}
	}

	public static void registerKeyBinding() {
		ClientRegistry.registerKeyBinding(CLEAR_CHAT);
	}
}
