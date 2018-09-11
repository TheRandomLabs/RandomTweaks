package com.therandomlabs.randomtweaks.common;

import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RandomTweaks.MODID)
public final class RespawnHandler {
	public enum HungerResetBehavior {
		RESET,
		DONT_RESET,
		RESET_UNLESS_KEEP_INVENTORY
	}

	public enum DeathPunishmentMode {
		ENABLED,
		ENABLED_IF_KEEP_INVENTORY,
		DISABLED
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

		final DeathPunishmentMode mode = RTConfig.misc.deathPunishmentMode;

		if(!player.capabilities.isCreativeMode &&
				((keepInventory && mode == DeathPunishmentMode.ENABLED_IF_KEEP_INVENTORY) ||
				mode == DeathPunishmentMode.ENABLED)) {
			newFoodLevel -= 3;
			player.experience = 0;
			player.experienceLevel = 0;
			player.experienceTotal = 0;
		}

		final int minimum = RTConfig.hunger.minimumRespawnHungerLevel;
		final FoodStats newStats = player.getFoodStats();

		newStats.foodLevel = newFoodLevel < minimum ? minimum : newFoodLevel;
		newStats.foodSaturationLevel = oldSaturationLevel;
	}

	public static boolean resetHungerOnRespawn(EntityPlayer player, boolean keepInventory) {
		if(player.capabilities.isCreativeMode) {
			return true;
		}

		final DeathPunishmentMode mode = RTConfig.misc.deathPunishmentMode;

		//deathPunishmentMode overrides respawnResetBehavior
		if((keepInventory && mode == DeathPunishmentMode.ENABLED_IF_KEEP_INVENTORY) ||
				mode == DeathPunishmentMode.ENABLED) {
			return false;
		}

		switch(RTConfig.hunger.respawnResetBehavior) {
			case RESET:
				return true;
			case DONT_RESET:
				return false;
			default:
				return keepInventory;
		}
	}
}
