package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.common.RTConfig;
import com.therandomlabs.randomtweaks.common.RandomTweaks;
import com.therandomlabs.randomtweaks.util.Compat;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public class MiscClientEventHandler {
	public static final KeyBinding CLEAR_CHAT =
			new KeyBinding("key.clearChat", Keyboard.KEY_I, "key.categories.randomtweaks");
	public static final KeyBinding NOCLIP =
			new KeyBinding("key.noclip", Keyboard.KEY_F4, "key.categories.randomtweaks");

	@SubscribeEvent
	public static void onKeyInput(InputEvent.KeyInputEvent event) {
		if(!Keyboard.getEventKeyState()) {
			return;
		}

		final int key = Keyboard.getEventKey();

		if(CLEAR_CHAT.isActiveAndMatches(key)) {
			final GuiIngame ingameGUI = Minecraft.getMinecraft().ingameGUI;
			if(ingameGUI != null) {
				Compat.clearChatMessages(ingameGUI.getChatGUI());
			}
			return;
		}

		if(NOCLIP.isActiveAndMatches(key)) {
			final EntityPlayerSP player = Minecraft.getMinecraft().player;
			final String gamemode = player.isCreative() ? "sp" : "c";
			player.sendChatMessage("/gamemode " + gamemode);
		}
	}

	public static void registerKeyBindings() {
		if(RTConfig.client.clearChatKeybind) {
			ClientRegistry.registerKeyBinding(CLEAR_CHAT);
		}

		if(RTConfig.client.noclipKeybind) {
			ClientRegistry.registerKeyBinding(NOCLIP);
		}
	}

	@SubscribeEvent
	public static void onRenderFog(EntityViewRenderEvent.FogDensity event) {
		if(RTConfig.client.clearWater && event.getState().getMaterial() == Material.WATER) {
			event.setDensity(0);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onPotionShift(GuiScreenEvent.PotionShiftEvent event) {
		if(RTConfig.client.disablePotionShift) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onSoundPlay(PlaySoundEvent event) {
		if(RTConfig.client.disableWitherSpawnSound &&
				event.getName().equals("entity.wither.spawn")) {
			event.setResultSound(null);
			return;
		}

		if(RTConfig.client.disableEnderDragonDeathSound &&
				event.getName().equals("entity.enderdragon.death")) {
			event.setResultSound(null);
		}
	}
}
