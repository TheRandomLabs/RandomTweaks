package com.therandomlabs.randomtweaks.client;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.Random;
import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import paulscode.sound.SoundSystem;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MOD_ID)
public final class DingHandler {
	private static final Random random = new SecureRandom();
	private static final Minecraft mc = Minecraft.getMinecraft();

	//DSurround "Mute when Background" feature compatibility
	private static float previousVolume;
	private static ISound currentlyPlaying;

	private static boolean playWorld;

	public static void onGameStarted() {
		if(!RandomTweaks.DING_LOADED && RTConfig.ding.startupSounds.length != 0 &&
				isDsurroundStartupSoundDisabled()) {
			final int index = random.nextInt(RTConfig.ding.startupSoundEvents.length);
			playSound(RTConfig.ding.startupSoundEvents[index], RTConfig.ding.startupSoundPitch);
		}
	}

	public static void onClientTick() {
		if(currentlyPlaying == null) {
			return;
		}

		final SoundSystem system = getSoundSystem();

		if(mc.getSoundHandler().sndManager.isSoundPlaying(currentlyPlaying)) {
			system.setMasterVolume(mc.gameSettings.getSoundLevel(SoundCategory.MASTER));
			return;
		}

		currentlyPlaying = null;
		system.setMasterVolume(previousVolume);
	}

	@SubscribeEvent
	public static void onConnectToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		if(!RandomTweaks.DING_LOADED && RTConfig.ding.worldLoadSounds.length != 0) {
			playWorld = true;
		}
	}

	@SubscribeEvent
	public static void onWorldTick(TickEvent.WorldTickEvent event) {
		if(!playWorld || event.phase != TickEvent.Phase.END || mc.player == null) {
			return;
		}

		if(mc.player.ticksExisted > 20 || mc.isGamePaused()) {
			final int index = random.nextInt(RTConfig.ding.worldLoadSoundEvents.length);
			playSound(RTConfig.ding.worldLoadSoundEvents[index], RTConfig.ding.worldLoadSoundPitch);
			playWorld = false;
		}
	}

	public static void playSound(SoundEvent soundEvent, double pitch) {
		final SoundHandler soundHandler = mc.getSoundHandler();

		previousVolume = getSoundSystem().getMasterVolume();
		currentlyPlaying = PositionedSoundRecord.getMasterRecord(soundEvent, (float) pitch);

		soundHandler.playSound(currentlyPlaying);

		onClientTick();
	}

	public static SoundSystem getSoundSystem() {
		return mc.getSoundHandler().sndManager.sndSystem;
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
