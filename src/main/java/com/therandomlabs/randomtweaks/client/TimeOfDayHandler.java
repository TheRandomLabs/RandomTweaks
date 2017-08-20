package com.therandomlabs.randomtweaks.client;

import java.io.File;
import java.util.Locale;
import org.lwjgl.input.Keyboard;
import com.therandomlabs.randomtweaks.common.RTConfig;
import com.therandomlabs.randomtweaks.common.RandomTweaks;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraft.client.Minecraft;
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

//Some of the code in this class has been taken and adapted from here:
//https://github.com/Lunatrius/InGame-Info-XML
@EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public class TimeOfDayHandler {
	public static final KeyBinding TOGGLE_TIME_OF_DAY_OVERLAY = new KeyBinding(
			"key.toggleTimeOfDayOverlay", Keyboard.KEY_BACKSLASH, "key.categories.randomtweaks");
	private static boolean shouldDraw;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		shouldDraw = true;
	}

	@SubscribeEvent
	public static void onRenderTick(TickEvent.RenderTickEvent event) {
		if(!shouldDraw) {
			return;
		}

		final Minecraft mc = Minecraft.getMinecraft();

		if(!shouldDraw(mc.world)) {
			return;
		}

		final long time = mc.world.getWorldTime();

		long hour = (time / 1000 + 6) % 24;

		if(RTConfig.timeofday.twentyFourHourTime) {
			mc.fontRenderer.drawStringWithShadow(String.format(
					Locale.ENGLISH, "%s %d, %02d:%02d (%s)",
					Utils.localize("timeOfDayOverlay.day"),
					time / 24000 + 1, //Day
					hour, //Hour (minute IRL)
					(time % 1000) * 60 / 1000, //Minute (second IRL)
					dayOrNight(mc.world)
			), 2.0F, 2.0F, 0xFFFFFF);
		} else {
			String ampm = Utils.localize("timeOfDayOverlay.am");

			if(hour >= 12) {
				hour -= 12;
				ampm = Utils.localize("timeOfDayOverlay.pm");
			}

			if(hour == 0) {
				hour = 12;
			}

			mc.fontRenderer.drawStringWithShadow(String.format(
					Locale.ENGLISH, "%s %d, %02d:%02d %s (%s)",
					Utils.localize("timeOfDayOverlay.day"),
					time / 24000 + 1, //Day
					hour, //Hour (minute IRL)
					(time % 1000) * 60 / 1000, //Minute (second IRL)
					ampm,
					dayOrNight(mc.world)
			), 2.0F, 2.0F, 0xFFFFFF);
		}
	}

	@SubscribeEvent
	public static void onKeyInput(KeyInputEvent event) {
		if(Keyboard.getEventKeyState() &&
				TOGGLE_TIME_OF_DAY_OVERLAY.isActiveAndMatches(Keyboard.getEventKey())) {
			final Minecraft mc = Minecraft.getMinecraft();
			final World world = mc.world;

			final File saveDirectory = DimensionManager.getCurrentSaveRootDirectory();
			if(saveDirectory != null) {
				RTConfig.TimeOfDay.worlds.put(saveDirectory.getName(), !isEnabledForWorld(world));
			} else {
				RTConfig.TimeOfDay.worlds.put(mc.getCurrentServerData().serverIP,
						!isEnabledForWorld(world));
			}
			RTConfig.TimeOfDay.saveWorlds();
		}
	}

	public static void registerKeyBinding() {
		ClientRegistry.registerKeyBinding(TOGGLE_TIME_OF_DAY_OVERLAY);
	}

	public static boolean shouldDraw(World world) {
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

		return isEnabledForWorld(world);
	}

	public static boolean isEnabledForWorld(World world) {
		final File saveDirectory = DimensionManager.getCurrentSaveRootDirectory();
		if(saveDirectory != null) {
			final String name = saveDirectory.getName();
			if(!RTConfig.TimeOfDay.worlds.containsKey(name)) {
				RTConfig.TimeOfDay.worlds.put(name, RTConfig.timeofday.enabledByDefault);
				RTConfig.TimeOfDay.saveWorlds();
			}
			return RTConfig.TimeOfDay.worlds.get(name);
		}

		final String ip = Minecraft.getMinecraft().getCurrentServerData().serverIP;

		if(ip == null) {
			return false;
		}

		if(!RTConfig.TimeOfDay.worlds.containsKey(ip)) {
			RTConfig.TimeOfDay.worlds.put(ip, RTConfig.timeofday.enabledByDefault);
			RTConfig.TimeOfDay.saveWorlds();
		}
		return RTConfig.TimeOfDay.worlds.get(ip);
	}

	public static String dayOrNight(World world) {
		return Utils.localize(String.format("timeOfDayOverlay.%sTime",
				world.calculateSkylightSubtracted(1.0F) < 4 ? "day" : "night"));
	}
}
