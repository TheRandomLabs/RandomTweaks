package com.therandomlabs.randomtweaks.common;

import java.io.IOException;
import java.lang.reflect.Field;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public final class HungerHandler {
	public static final Field SATURATION_LEVEL =
			ReflectionHelper.findField(FoodStats.class, "foodSaturationLevel", "field_75125_b");

	@SubscribeEvent
	public void onDeath(LivingDeathEvent event) {
		if(event.getEntity() instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) event.getEntity();
			final NBTTagCompound data = player.getEntityData();
			data.setInteger("DeathFoodLevel", player.getFoodStats().getFoodLevel());
			data.setFloat("DeathSaturationLevel", player.getFoodStats().getSaturationLevel());
		}
	}

	@SubscribeEvent
	public void onRespawn(PlayerEvent.Clone event) throws Exception {
		final NBTTagCompound oldPlayer = event.getOriginal().getEntityData();
		final EntityPlayer player = event.getEntityPlayer();

		final int deathFoodLevel = oldPlayer.getInteger("DeathFoodLevel");
		final int minimum = ConfigurationHandler.readInteger("minimumHungerLevelOnRespawn");
		player.getFoodStats().setFoodLevel(deathFoodLevel < minimum ? minimum : deathFoodLevel);
		setSaturation(player.getFoodStats(),
				player.getEntityData().getFloat("DeathSaturationLevel"));
	}

	public static void setSaturation(FoodStats stats, float saturation) throws Exception {
		SATURATION_LEVEL.set(stats, saturation);
	}
}
