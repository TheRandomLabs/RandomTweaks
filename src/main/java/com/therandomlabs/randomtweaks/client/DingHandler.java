package com.therandomlabs.randomtweaks.client;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.Random;
import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MOD_ID)
public final class DingHandler {
	private static final Random random = new SecureRandom();
	private static final Minecraft mc = Minecraft.getMinecraft();

	private static boolean playWorld;

	public static void onGameStarted() {
		if(RTConfig.ding.soundNames.length != 0 && isDsurroundStartupSoundDisabled()) {
			final int index = random.nextInt(RTConfig.ding.soundNames.length);
			playSound(RTConfig.ding.soundNames[index], RTConfig.ding.soundPitch);
		}
	}

	@SubscribeEvent
	public static void onConnectToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		if(RTConfig.ding.worldSoundNames.length != 0) {
			playWorld = true;
		}
	}

	@SubscribeEvent
	public static void onWorldTick(TickEvent.WorldTickEvent event) {
		if(!playWorld || event.phase != TickEvent.Phase.END || mc.player == null) {
			return;
		}

		if(mc.player.ticksExisted > 20 || mc.isGamePaused()) {
			final int index = random.nextInt(RTConfig.ding.worldSoundNames.length);
			playSound(RTConfig.ding.worldSoundNames[index], RTConfig.ding.worldSoundPitch);

			playWorld = false;
		}
	}

	public static void playSound(String soundName, double pitch) {
		if(RandomTweaks.DING_LOADED || soundName.isEmpty()) {
			return;
		}

		final ResourceLocation resource = new ResourceLocation(soundName);
		final SoundEvent sound = SoundEvent.REGISTRY.getObject(resource);

		if(sound != null) {
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(
					sound, (float) pitch
			));
		} else {
			RandomTweaks.LOGGER.error("Sound does not exist: %s", resource);
		}
	}

	public static boolean isDsurroundStartupSoundDisabled() {
		if(!RandomTweaks.DYNAMIC_SURROUNDINGS_LOADED) {
			return true;
		}

		try {
			Class<?> general;

			try {
				general = Class.forName("org.orecruncher.dsurround.ModOptions$general");
			} catch(ClassNotFoundException ex) {
				general = Class.forName("org.blockartistry.DynSurround.ModOptions$general");
			}

			if(general != null) {
				final Field startupSoundListField = general.getDeclaredField("startupSoundList");
				final String[] startupSoundList = (String[]) startupSoundListField.get(null);

				return startupSoundList.length == 0;
			}
		} catch(Exception ex) {
			RandomTweaks.LOGGER.error(
					"Failed to check if Dynamic Surrounding's startup sound list is empty", ex
			);
		}

		return true;
	}
}
