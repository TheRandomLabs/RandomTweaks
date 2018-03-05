package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.common.RandomTweaks;
import com.therandomlabs.randomtweaks.util.Compat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

@EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public final class ClearChatHandler {
	public static final KeyBinding CLEAR_CHAT =
			new KeyBinding("key.clearChat", Keyboard.KEY_I, "key.categories.randomtweaks");

	@SubscribeEvent
	public static void onKeyInput(KeyInputEvent event) {
		if(Keyboard.getEventKeyState() && CLEAR_CHAT.isActiveAndMatches(Keyboard.getEventKey()) &&
				Minecraft.getMinecraft().ingameGUI != null) {
			Compat.clearChatMessages(Minecraft.getMinecraft().ingameGUI.getChatGUI());
		}
	}

	public static void registerKeyBinding() {
		ClientRegistry.registerKeyBinding(CLEAR_CHAT);
	}
}
