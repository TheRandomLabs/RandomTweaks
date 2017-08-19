package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.common.RTConfig;
import com.therandomlabs.randomtweaks.common.RandomTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

//Idea and a lot of the code taken from:
//https://github.com/iChun/Ding/blob/master/src/main/java/me/ichun/mods/ding/common/core/Ding.java
//I do not take any credit, and I will remove this if asked.
@EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public class DingHandler {
	private static boolean mainMenuPlayed;
	private static boolean playWorld;

	@SubscribeEvent
	public static void onGuiOpen(GuiOpenEvent event) {
		if(event.getGui() instanceof GuiMainMenu && !mainMenuPlayed) {
			mainMenuPlayed = true;
			playSound(RTConfig.ding.soundName, RTConfig.ding.soundPitch);
		}
	}

	@SubscribeEvent
	public static void onConnectToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		if(!RTConfig.ding.worldSoundName.isEmpty()) {
			playWorld = true;
		}
	}

	@SubscribeEvent
	public static void onWorldTick(TickEvent.WorldTickEvent event) {
		if(playWorld && event.phase == TickEvent.Phase.END) {
			final Minecraft mc = Minecraft.getMinecraft();
			final EntityPlayer player = mc.player;

			if(player != null && (player.ticksExisted > 20 || mc.isGamePaused())) {
				playSound(RTConfig.ding.worldSoundName, RTConfig.ding.worldSoundPitch);
				playWorld = false;
			}
		}
	}

	public static void playSound(String soundName, double pitch) {
		if(RTConfig.ding.disableIfDingIsInstalled && Loader.isModLoaded("ding")) {
			return;
		}

		if(soundName.isEmpty()) {
			return;
		}

		final ResourceLocation resource = new ResourceLocation(soundName);
		final SoundEvent sound = SoundEvent.REGISTRY.getObject(resource);

		if(sound != null) {
			Minecraft.getMinecraft().getSoundHandler().playSound(
					PositionedSoundRecord.getMasterRecord(sound, (float) pitch));
		} else {
			RandomTweaks.LOGGER.error("Could not find sound: %s", resource);
		}
	}
}
