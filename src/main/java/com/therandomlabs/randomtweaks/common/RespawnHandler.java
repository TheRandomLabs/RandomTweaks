package com.therandomlabs.randomtweaks.common;

import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = RandomTweaks.MODID)
public final class RespawnHandler {
	public enum HungerBehavior {
		RESET,
		DONT_RESET,
		RESET_UNLESS_KEEPINVENTORY;
	}

	@SubscribeEvent
	public static void onRespawn(PlayerEvent.Clone event) throws Exception {
		final boolean keepInventory =
				event.getOriginal().getEntityWorld().getGameRules().getBoolean("keepInventory");

		if(!dontResetHungerOnRespawn(event.getOriginal(), keepInventory)) {
			return;
		}

		final EntityPlayer player = event.getEntityPlayer();

		final FoodStats stats = event.getOriginal().getFoodStats();
		final int oldFoodLevel = stats.getFoodLevel();
		final float oldSaturationLevel = stats.getSaturationLevel();

		int newFoodLevel = oldFoodLevel;

		if(keepInventory && !player.capabilities.isCreativeMode &&
				RTConfig.respawn.deathPunishmentsIfKeepInventory) {
			newFoodLevel -= 3;
			player.experience = 0;
			player.experienceLevel = 0;
			player.experienceTotal = 0;
		}

		final int minimum = RTConfig.respawn.minimumHungerLevel;
		player.getFoodStats().setFoodLevel(newFoodLevel < minimum ? minimum : newFoodLevel);
		Utils.setSaturation(player.getFoodStats(), oldSaturationLevel);
	}

	public static boolean dontResetHungerOnRespawn(EntityPlayer player, boolean keepInventory) {
		switch(RTConfig.respawn.hungerBehavior) {
		case RESET:
			return keepInventory && RTConfig.respawn.deathPunishmentsIfKeepInventory;
		case DONT_RESET:
			return true;
		case RESET_UNLESS_KEEPINVENTORY:
			return keepInventory && !player.capabilities.isCreativeMode;
		}
		return false;
	}
}
