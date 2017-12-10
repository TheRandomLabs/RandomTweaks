package com.therandomlabs.randomtweaks.client;

import java.io.File;
import org.lwjgl.input.Keyboard;
import com.therandomlabs.randomtweaks.common.RTConfig;
import com.therandomlabs.randomtweaks.common.RandomTweaks;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

//Some code has been taken and adapted from here:
//https://github.com/Lunatrius/InGame-Info-XML
@EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public class TimeOfDayHandler {
	public static final KeyBinding TOGGLE_TIME_OF_DAY_OVERLAY = new KeyBinding(
			"key.toggleTimeOfDayOverlay", Keyboard.KEY_BACKSLASH, "key.categories.randomtweaks");

	private static final Minecraft mc = Minecraft.getMinecraft();
	private static boolean shouldDraw;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		shouldDraw = !mc.gameSettings.showDebugInfo && !mc.gameSettings.hideGUI;
	}

	@SubscribeEvent
	public static void onRenderTick(TickEvent.RenderTickEvent event) {
		if(!shouldDraw || !shouldDraw() || !isEnabled()) {
			return;
		}

		final World world = mc.player.getEntityWorld();

		final long time = world.getWorldTime();
		long hour = (time / 1000L + 6L) % 24L;
		final long minute = (time % 1000L) * 60L / 1000L;
		final long day = time / 24000L + 1L;

		final String ampm;

		if(RTConfig.timeofday.twentyFourHourTime) {
			ampm = "";
		} else {
			if(hour >= 12) {
				hour -= 12;
				ampm = " " + Utils.localize("timeOfDayOverlay.pm");
			} else {
				//Midnight
				if(hour == 0) {
					hour = 12;
				}

				ampm = " " + Utils.localize("timeOfDayOverlay.pm");
			}
		}

		final String hourString = hour < 10 ? "0" + hour : Long.toString(hour);
		final String minuteString = minute < 10 ? "0" + minute : Long.toString(minute);

		final String dayOrNight;

		if(world.calculateSkylightSubtracted(1.0F) < 4) {
			dayOrNight = Utils.localize("timeOfDayOverlay.dayTime");
		} else {
			dayOrNight = Utils.localize("timeOfDayOverlay.nightTime");
		}

		final String timeString = Utils.localize("timeOfDayOverlay.day") + " " +
				day + ", " +
				hourString + ":" + minuteString +
				ampm +
				" (" + dayOrNight + ")";

		mc.fontRenderer.drawStringWithShadow(timeString, 2.0F, 2.0F, 0xFFFFFF);
	}

	@SubscribeEvent
	public static void onKeyInput(KeyInputEvent event) {
		if(Keyboard.getEventKeyState() &&
				TOGGLE_TIME_OF_DAY_OVERLAY.isActiveAndMatches(Keyboard.getEventKey())) {
			if(!shouldDraw()) {
				return;
			}

			final File saveDirectory = DimensionManager.getCurrentSaveRootDirectory();
			if(saveDirectory != null) {
				RTConfig.TimeOfDay.worlds.put(saveDirectory.getName(), !isEnabled());
			} else {
				RTConfig.TimeOfDay.worlds.put(mc.getCurrentServerData().serverIP, !isEnabled());
			}

			RTConfig.TimeOfDay.saveWorlds();
		}
	}

	public static void registerKeyBinding() {
		ClientRegistry.registerKeyBinding(TOGGLE_TIME_OF_DAY_OVERLAY);
	}

	public static boolean shouldDraw() {
		if(mc.player == null) {
			return false;
		}

		final World world = mc.player.getEntityWorld();

		if(world == null) {
			return false;
		}

		if(RTConfig.timeofday.disableIfNoDaylightCycle &&
				!world.getGameRules().getBoolean("doDaylightCycle")) {
			return false;
		}

		if(RTConfig.timeofday.disableInAdventureMode &&
				world.getWorldInfo().getGameType() == GameType.ADVENTURE) {
			return false;
		}

		return true;
	}

	public static boolean isEnabled() {
		final File saveDirectory = DimensionManager.getCurrentSaveRootDirectory();
		if(saveDirectory != null) {
			final String name = saveDirectory.getName();
			if(!RTConfig.TimeOfDay.worlds.containsKey(name)) {
				RTConfig.TimeOfDay.worlds.put(name, RTConfig.timeofday.enabledByDefault);
				RTConfig.TimeOfDay.saveWorlds();
			}
			return RTConfig.TimeOfDay.worlds.get(name);
		}

		final ServerData serverData = Minecraft.getMinecraft().getCurrentServerData();
		if(serverData == null) {
			return false;
		}

		final String ip = serverData.serverIP;
		if(ip == null) {
			return false;
		}

		if(!RTConfig.TimeOfDay.worlds.containsKey(ip)) {
			RTConfig.TimeOfDay.worlds.put(ip, RTConfig.timeofday.enabledByDefault);
			RTConfig.TimeOfDay.saveWorlds();
		}
		return RTConfig.TimeOfDay.worlds.get(ip);
	}
}
