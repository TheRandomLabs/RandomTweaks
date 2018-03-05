package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.common.RandomTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

@EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public final class NoclipHandler {
	public static final KeyBinding NOCLIP =
			new KeyBinding("key.noclip", Keyboard.KEY_F4, "key.categories.randomtweaks");

	@SubscribeEvent
	public static void onKeyInput(KeyInputEvent event) {
		if(Keyboard.getEventKeyState() && NOCLIP.isActiveAndMatches(Keyboard.getEventKey())) {
			final EntityPlayerSP player = Minecraft.getMinecraft().player;
			final String gamemode = player.isCreative() ? "sp" : "c";
			player.sendChatMessage("/gamemode " + gamemode);
		}
	}

	public static void registerKeyBinding() {
		ClientRegistry.registerKeyBinding(NOCLIP);
	}
}
