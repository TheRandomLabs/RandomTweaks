package com.therandomlabs.randomtweaks.common;

import java.lang.reflect.Field;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@EventBusSubscriber(modid = RandomTweaks.MODID)
public final class HungerHandler {
	public static final Field SATURATION_LEVEL =
			ReflectionHelper.findField(FoodStats.class, "foodSaturationLevel", "field_75125_b");

	@SubscribeEvent
	public static void onDeath(LivingDeathEvent event) {
		if(event.getEntity() instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) event.getEntity();

			if(!dontResetHungerOnRespawn(player)) {
				return;
			}

			final NBTTagCompound data = player.getEntityData();
			data.setInteger("DeathFoodLevel", player.getFoodStats().getFoodLevel());
			data.setFloat("DeathSaturationLevel", player.getFoodStats().getSaturationLevel());
		}
	}

	@SubscribeEvent
	public static void onRespawn(PlayerEvent.Clone event) throws Exception {
		if(!dontResetHungerOnRespawn(event.getOriginal())) {
			return;
		}

		final NBTTagCompound oldPlayer = event.getOriginal().getEntityData();
		final EntityPlayer player = event.getEntityPlayer();

		final int deathFoodLevel = oldPlayer.getInteger("DeathFoodLevel");
		final int minimum = RTConfig.hunger.minimumHungerLevel;
		player.getFoodStats().setFoodLevel(deathFoodLevel < minimum ? minimum : deathFoodLevel);
		setSaturation(player.getFoodStats(),
				player.getEntityData().getFloat("DeathSaturationLevel"));
	}

	public static void setSaturation(FoodStats stats, float saturation) throws Exception {
		SATURATION_LEVEL.set(stats, saturation);
	}

	public static boolean dontResetHungerOnRespawn(EntityPlayer player) {
		switch(RTConfig.hunger.respawnBehavior) {
		case RTConfigConstants.Hunger.RESET_ON_RESPAWN:
			return false;
		case RTConfigConstants.Hunger.DONT_RESET_ON_RESPAWN:
			return true;
		case RTConfigConstants.Hunger.RESET_UNLESS_KEEPINVENTORY:
			return player.getEntityWorld().getGameRules().getBoolean("keepInventory");
		case RTConfigConstants.Hunger.RESET_UNLESS_KEEPINVENTORY_OR_CREATIVE:
			return player.getEntityWorld().getGameRules().getBoolean("keepInventory") &&
					!player.capabilities.isCreativeMode;
		}
		return false;
	}
}
