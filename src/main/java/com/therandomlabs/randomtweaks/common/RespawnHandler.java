package com.therandomlabs.randomtweaks.common;

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
		RESET_UNLESS_KEEPINVENTORY
	}

	@SubscribeEvent
	public static void onRespawn(PlayerEvent.Clone event) throws Exception {
		final EntityPlayer original = event.getOriginal();

		final boolean keepInventory =
				original.getEntityWorld().getGameRules().getBoolean("keepInventory");

		if(resetHungerOnRespawn(original, keepInventory)) {
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

		final FoodStats newStats = player.getFoodStats();
		newStats.setFoodLevel(newFoodLevel < minimum ? minimum : newFoodLevel);
		newStats.foodSaturationLevel = oldSaturationLevel;
	}

	public static boolean resetHungerOnRespawn(EntityPlayer player, boolean keepInventory) {
		if(keepInventory && RTConfig.respawn.deathPunishmentsIfKeepInventory) {
			return false;
		}

		switch(RTConfig.respawn.hungerBehavior) {
			case RESET:
				return true;
			case DONT_RESET:
				return false;
			case RESET_UNLESS_KEEPINVENTORY:
				//In creative mode, hunger doesn't matter anyway
				return player.capabilities.isCreativeMode || keepInventory;
		}

		throw new UnsupportedOperationException("This should be impossible");
	}
}
