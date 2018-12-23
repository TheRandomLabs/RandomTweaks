package com.therandomlabs.randomtweaks.client;

import java.lang.reflect.Field;
import java.util.Random;
import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import paulscode.sound.SoundSystem;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MOD_ID)
public final class DingHandler {
	private static final Class<?> GENERAL_MOD_OPTIONS;
	private static final Class<?> BACKGROUND_MUTE;

	private static final Random random = new Random();
	private static final Minecraft mc = Minecraft.getMinecraft();

	private static boolean playWorld;

	private static ISound sound;

	static {
		if(RandomTweaks.DYNAMIC_SURROUNDINGS_LOADED) {
			GENERAL_MOD_OPTIONS = getDsurroundClass("ModOptions$general");
			BACKGROUND_MUTE = getDsurroundClass("client.sound.BackgroundMute");
		} else {
			GENERAL_MOD_OPTIONS = null;
			BACKGROUND_MUTE = null;
		}
	}

	public static void onGameStarted() {
		if(!RandomTweaks.DING_LOADED && RTConfig.ding.startupSounds.length != 0 &&
				isDsurroundStartupSoundDisabled()) {
			final int index = random.nextInt(RTConfig.ding.startupSoundEvents.length);
			playSound(RTConfig.ding.startupSoundEvents[index], RTConfig.ding.startupSoundPitch);
		}
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

	public static void onClientTick() {
		if(sound == null) {
			return;
		}

		//Dynamics Surroundings compatibility

		final SoundHandler soundHandler = mc.getSoundHandler();

		if(!soundHandler.isSoundPlaying(sound)) {
			MinecraftForge.EVENT_BUS.register(BACKGROUND_MUTE);
		}
	}

	public static void playSound(SoundEvent soundEvent, double pitch) {
		final SoundHandler soundHandler = mc.getSoundHandler();
		sound = PositionedSoundRecord.getMasterRecord(soundEvent, (float) pitch);

		if(!RTConfig.ding.ignoreDsurroundMuteWhenBackground || BACKGROUND_MUTE == null) {
			soundHandler.playSound(sound);
			sound = null;
			return;
		}

		//Dynamics Surroundings compatibility

		((SoundSystem) soundHandler.sndManager.sndSystem).setMasterVolume(
				mc.gameSettings.getSoundLevel(SoundCategory.MASTER)
		);

		soundHandler.playSound(sound);

		MinecraftForge.EVENT_BUS.unregister(BACKGROUND_MUTE);
	}

	public static boolean isDsurroundStartupSoundDisabled() {
		if(GENERAL_MOD_OPTIONS == null) {
			return true;
		}

		try {
			final Field startupSoundListField =
					GENERAL_MOD_OPTIONS.getDeclaredField("startupSoundList");
			final String[] startupSoundList = (String[]) startupSoundListField.get(null);

			return startupSoundList.length == 0;
		} catch(Exception ex) {
			RandomTweaks.LOGGER.error(
					"Failed to check if Dynamic Surrounding's startup sound list is empty", ex
			);
		}

		return true;
	}

	public static Class<?> getDsurroundClass(String name) {
		try {
			return Class.forName("org.orecruncher.dsurround." + name);
		} catch(ClassNotFoundException ex) {
			try {
				return Class.forName("org.blockartistry.DynSurround." + name);
			} catch(ClassNotFoundException ex2) {
				RandomTweaks.LOGGER.error(
						"Failed to find Dynamic Surroundings class: " + name, ex2
				);
			}
		}

		return null;
	}
}
