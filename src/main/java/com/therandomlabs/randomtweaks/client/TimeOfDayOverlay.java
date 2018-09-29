package com.therandomlabs.randomtweaks.client;

import java.io.File;
import java.util.Map;
import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.util.RTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

//Some code has been taken and adapted from https://github.com/Lunatrius/InGame-Info-XML
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public final class TimeOfDayOverlay {
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static boolean shouldHide;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		shouldHide = mc.gameSettings.showDebugInfo || mc.gameSettings.hideGUI;
	}

	@SubscribeEvent
	public static void onRenderTick(TickEvent.RenderTickEvent event) {
		if(shouldHide() || !isEnabledForCurrentWorld()) {
			return;
		}

		final World world = mc.player.getEntityWorld();

		final long time = world.getWorldTime();
		long hour = (time / 1000L + 6L) % 24L;
		final long minute = (time % 1000L) * 60L / 1000L;
		final long day = time / 24000L + 1L;

		final String ampm;

		if(RTConfig.timeOfDay.twentyFourHourTime) {
			ampm = "";
		} else {
			if(hour >= 12) {
				if(hour != 12) {
					hour -= 12;
				}
				ampm = " " + RTUtils.localize("timeOfDayOverlay.pm");
			} else {
				//Midnight
				if(hour == 0) {
					hour = 12;
				}

				ampm = " " + RTUtils.localize("timeOfDayOverlay.am");
			}
		}

		final String hourString = hour < 10 ? "0" + hour : Long.toString(hour);
		final String minuteString = minute < 10 ? "0" + minute : Long.toString(minute);

		final String dayOrNight;

		if(world.calculateSkylightSubtracted(1.0F) < 4) {
			dayOrNight = RTUtils.localize("timeOfDayOverlay.dayTime");
		} else {
			dayOrNight = RTUtils.localize("timeOfDayOverlay.nightTime");
		}

		final String timeString = RTUtils.localize("timeOfDayOverlay.day") + " " +
				day + ", " +
				hourString + ":" + minuteString +
				ampm +
				" (" + dayOrNight + ")";

		final int textWidth = mc.fontRenderer.getStringWidth(timeString);
		final int textHeight = mc.fontRenderer.FONT_HEIGHT;

		final int x = RTConfig.timeOfDay.x;
		final int y = RTConfig.timeOfDay.y;

		final ScaledResolution scaled = new ScaledResolution(mc);
		final int displayWidth = scaled.getScaledWidth();
		final int displayHeight = scaled.getScaledHeight();

		final int actualX = RTConfig.timeOfDay.alignment.getX(x, displayWidth, textWidth);
		final int actualY = RTConfig.timeOfDay.alignment.getY(y, displayHeight, textHeight);

		mc.fontRenderer.drawStringWithShadow(timeString, actualX, actualY, 0xFFFFFF);
	}

	public static boolean shouldHide() {
		if(!RTConfig.timeOfDay.enabled || shouldHide || mc.world == null ||
				!Minecraft.isGuiEnabled()) {
			return true;
		}

		if(mc.currentScreen != null && RTConfig.timeOfDay.disableInGUIs) {
			return true;
		}

		if(RTConfig.timeOfDay.disableIfNoDaylightCycle &&
				!mc.world.getGameRules().getBoolean("doDaylightCycle")) {
			return true;
		}

		return RTConfig.timeOfDay.disableInAdventureMode &&
				mc.world.getWorldInfo().getGameType() == GameType.ADVENTURE;
	}

	public static boolean isEnabledForCurrentWorld() {
		final Map<String, Boolean> worlds = RTConfig.Data.get().timeOfDayOverlay;
		final File saveDirectory = DimensionManager.getCurrentSaveRootDirectory();

		if(saveDirectory != null) {
			final String name = saveDirectory.getName();

			if(!worlds.containsKey(name)) {
				worlds.put(name, RTConfig.timeOfDay.enabledByDefault);
				RTConfig.Data.save();
			}

			return worlds.get(name);
		}

		final ServerData serverData = mc.getCurrentServerData();

		if(serverData == null) {
			return false;
		}

		final String ip = serverData.serverIP;

		if(ip == null) {
			return false;
		}

		if(!RTConfig.Data.get().timeOfDayOverlay.containsKey(ip)) {
			worlds.put(ip, RTConfig.timeOfDay.enabledByDefault);
			RTConfig.Data.save();
		}

		return worlds.get(ip);
	}

	public static void toggle() {
		if(shouldHide()) {
			return;
		}

		final File saveDirectory = DimensionManager.getCurrentSaveRootDirectory();
		final Map<String, Boolean> worlds = RTConfig.Data.get().timeOfDayOverlay;
		final String name = saveDirectory != null ?
				saveDirectory.getName() : mc.getCurrentServerData().serverIP;

		if(!worlds.containsKey(name)) {
			worlds.put(name, !RTConfig.timeOfDay.enabledByDefault);
		} else {
			worlds.put(name, !worlds.get(name));
		}
	}
}
