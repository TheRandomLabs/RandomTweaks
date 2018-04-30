package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.common.RTConfig;
import com.therandomlabs.randomtweaks.common.RandomTweaks;
import com.therandomlabs.randomtweaks.util.Compat;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public final class MiscClientEventHandler {
	public static final KeyBinding CLEAR_CHAT = new KeyBinding("key.clearChat",
			KeyConflictContext.UNIVERSAL, KeyModifier.CONTROL, Keyboard.KEY_I,
			"key.categories.randomtweaks");
	public static final KeyBinding NOCLIP = new KeyBinding("key.noclip",
			KeyConflictContext.UNIVERSAL, KeyModifier.SHIFT, Keyboard.KEY_F4,
			"key.categories.randomtweaks");
	public static final KeyBinding TOGGLE_FOV_CHANGES = new KeyBinding("key.toggleFoVChanges",
			KeyConflictContext.UNIVERSAL, KeyModifier.SHIFT, Keyboard.KEY_COMMA,
			"key.categories.randomtweaks");

	private static boolean fovChangesEnabled = RTConfig.client.fovChangesEnabledByDefault;

	@SubscribeEvent
	public static void onKeyInput(InputEvent.KeyInputEvent event) {
		if(!Keyboard.getEventKeyState()) {
			return;
		}

		final int key = Keyboard.getEventKey();

		if(CLEAR_CHAT.isActiveAndMatches(key)) {
			if(RTConfig.client.clearChatKeybind) {
				final GuiIngame ingameGUI = Minecraft.getMinecraft().ingameGUI;

				if(ingameGUI != null) {
					Compat.clearChatMessages(ingameGUI.getChatGUI());
				}
			}

			return;
		}

		if(NOCLIP.isActiveAndMatches(key)) {
			if(RTConfig.client.noclipKeybind) {
				final EntityPlayerSP player = Minecraft.getMinecraft().player;
				final String gamemode = player.isCreative() ? "sp" : "c";
				player.sendChatMessage("/gamemode " + gamemode);
			}

			return;
		}

		if(TOGGLE_FOV_CHANGES.isActiveAndMatches(key)) {
			if(RTConfig.client.toggleFoVChangesKeybind) {
				fovChangesEnabled = !fovChangesEnabled;

				if(RTConfig.client.fovChangesStatusMessage) {
					final EntityPlayerSP player = Minecraft.getMinecraft().player;
					Compat.sendStatusMessage(player, new TextComponentTranslation(
							"toggleFoVChanges." + (fovChangesEnabled ? "enabled" : "disabled")
					));
				}
			}

			return;
		}
	}

	public static void registerKeyBindings() {
		if(RTConfig.client.clearChatKeybind) {
			ClientRegistry.registerKeyBinding(CLEAR_CHAT);
		}

		if(RTConfig.client.noclipKeybind) {
			ClientRegistry.registerKeyBinding(NOCLIP);
		}

		if(RTConfig.client.toggleFoVChangesKeybind) {
			ClientRegistry.registerKeyBinding(TOGGLE_FOV_CHANGES);
		}
	}

	public static String onChat(String message) {
		if(!RTConfig.client.shortGamemodeCommands) {
			return message;
		}

		if(message.startsWith("/gms")) {
			message = message.replace("/gms", "/gamemode 0");
		} else if(message.startsWith("/gmc")) {
			message = message.replace("/gmc", "/gamemode 1");
		} else if(message.startsWith("/gma")) {
			message = message.replace("/gma", "/gamemode 2");
		} else if(message.startsWith("/gmsp")) {
			message = message.replace("/gmsp", "/gamemode 3");
		}

		return message;
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
		if(event.getName().equals("entity.wither.spawn")) {
			if(RTConfig.client.disableWitherSpawnSound) {
				event.setResultSound(null);
			}

			return;
		}

		if(event.getName().equals("entity.enderdragon.death")) {
			if(RTConfig.client.disableEnderDragonDeathSound) {
				event.setResultSound(null);
			}

			return;
		}
	}

	@SubscribeEvent
	public static void onFoVUpdate(FOVUpdateEvent event) {
		if(!fovChangesEnabled) {
			event.setNewfov(1.0F);
		}
	}
}
