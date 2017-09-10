package com.therandomlabs.randomtweaks.client;

import java.lang.reflect.Method;
import org.lwjgl.input.Keyboard;
import com.therandomlabs.randomtweaks.common.RandomTweaks;
import com.therandomlabs.randomtweaks.util.Compat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public class ClearChatHandler {
	public static final KeyBinding CLEAR_CHAT =
			new KeyBinding("key.clearChat", Keyboard.KEY_I, "key.categories.randomtweaks");
	private static final Method CLEAR_CHAT_MESSAGES = Compat.IS_ONE_POINT_TEN ?
			Compat.findMethod(GuiNewChat.class, "clearChatMessages", "func_146231_a") : null;

	@SubscribeEvent
	public static void onKeyInput(KeyInputEvent event) {
		if(Keyboard.getEventKeyState() && CLEAR_CHAT.isActiveAndMatches(Keyboard.getEventKey()) &&
				Minecraft.getMinecraft().ingameGUI != null) {
			final GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
			if(Compat.IS_ONE_POINT_TEN) {
				try {
					CLEAR_CHAT_MESSAGES.invoke(chat);
				} catch(Exception ex) {
					throw new ReportedException(
							new CrashReport("Could not clear chat mesages", ex));
				}
			} else {
				chat.clearChatMessages(false);
			}
		}
	}

	public static void registerKeyBinding() {
		ClientRegistry.registerKeyBinding(CLEAR_CHAT);
	}
}
