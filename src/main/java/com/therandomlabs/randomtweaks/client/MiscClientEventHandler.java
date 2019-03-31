package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.config.RTConfig;
import com.therandomlabs.randomtweaks.config.RTData;
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

			if(RTConfig.Client.startOnMultiplayerScreen) {
				mc.displayGuiScreen(new GuiMultiplayer(gui));
				event.setCanceled(true);
			}

			gameStarted = true;
		}
	}

	@SubscribeEvent
	public static void onRenderFog(EntityViewRenderEvent.FogDensity event) {
		if(RTConfig.Client.clearWater && event.getState().getMaterial() == Material.WATER) {
			event.setDensity(0);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onPotionShift(GuiScreenEvent.PotionShiftEvent event) {
		if(RTConfig.Client.disablePotionShift) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onSoundPlay(PlaySoundEvent event) {
		if(event.getName().equals("entity.wither.spawn")) {
			if(RTConfig.Client.disableWitherSpawnSound) {
				event.setResultSound(null);
			}

			return;
		}

		if(event.getName().equals("entity.enderdragon.death") &&
				RTConfig.Client.disableEnderDragonDeathSound) {
			event.setResultSound(null);
		}
	}

	@SubscribeEvent
	public static void onFoVUpdate(FOVUpdateEvent event) {
		if(!RTData.get().fovChanges) {
			event.setNewfov(1.0F);
		}
	}

	@SubscribeEvent
	public static void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
		if(RTConfig.Client.disablePotionIcons &&
				event.getType() == RenderGameOverlayEvent.ElementType.POTION_ICONS) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if(event.phase == TickEvent.Phase.END) {
			return;
		}

		AutoThirdPersonHandler.onClientTick();
		ArmorEquipSoundHandler.onClientTick();
		DingHandler.onClientTick();
	}
}
