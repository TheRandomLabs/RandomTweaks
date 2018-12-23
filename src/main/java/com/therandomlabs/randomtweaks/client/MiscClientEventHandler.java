package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MOD_ID)
public final class MiscClientEventHandler {
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static boolean gameStarted;

	@SubscribeEvent
	public static void onGuiOpen(GuiOpenEvent event) {
		final GuiScreen gui = event.getGui();

		if(!gameStarted && gui instanceof GuiMainMenu) {
			DingHandler.onGameStarted();

			if(RTConfig.client.startOnMultiplayerScreen) {
				mc.displayGuiScreen(new GuiMultiplayer(gui));
				event.setCanceled(true);
			}

			gameStarted = true;
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
		if(event.getName().equals("entity.wither.spawn")) {
			if(RTConfig.client.disableWitherSpawnSound) {
				event.setResultSound(null);
			}

			return;
		}

		if(event.getName().equals("entity.enderdragon.death") &&
				RTConfig.client.disableEnderDragonDeathSound) {
			event.setResultSound(null);
		}
	}

	@SubscribeEvent
	public static void onFoVUpdate(FOVUpdateEvent event) {
		if(!RTConfig.Data.get().fovChanges) {
			event.setNewfov(1.0F);
		}
	}

	@SubscribeEvent
	public static void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
		if(RTConfig.client.disablePotionIcons &&
				event.getType() == RenderGameOverlayEvent.ElementType.POTION_ICONS) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		AutoThirdPersonHandler.onClientTick();
		DingHandler.onClientTick();
	}
}
