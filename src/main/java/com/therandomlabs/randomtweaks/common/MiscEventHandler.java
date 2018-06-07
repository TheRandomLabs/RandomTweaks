package com.therandomlabs.randomtweaks.common;

import com.therandomlabs.randomtweaks.base.RTConfig;
import com.therandomlabs.randomtweaks.base.RandomTweaks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RandomTweaks.MODID)
public final class MiscEventHandler {
	@SubscribeEvent
	public static void onArrowImpact(ProjectileImpactEvent.Arrow event) {
		if(!RTConfig.general.pickUpSkeletonArrows) {
			return;
		}

		final EntityArrow arrow = event.getArrow();

		if(!arrow.getEntityWorld().isRemote && arrow.shootingEntity instanceof EntitySkeleton &&
				arrow.pickupStatus == EntityArrow.PickupStatus.DISALLOWED) {
			arrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
		final Entity entity = event.getEntity();

		if(entity.getEntityWorld().isRemote || !(entity instanceof EntityPlayer)) {
			return;
		}

		final EntityPlayer player = (EntityPlayer) event.getEntity();

		player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).
				setBaseValue(RTConfig.general.attackSpeed);
		player.foodStats = new RTFoodStats(player.foodStats);
	}

	@SubscribeEvent
	public static void onAnvilUpdate(AnvilUpdateEvent event) {
		if(RTConfig.general.disableCumulativeAnvilCost) {
			removeRepairCost(event.getLeft());
			removeRepairCost(event.getRight());
		}
	}

	@SubscribeEvent
	public static void onAnvilRepair(AnvilRepairEvent event) {
		if(RTConfig.general.disableCumulativeAnvilCost) {
			removeRepairCost(event.getItemResult());
		}
	}

	public static void removeRepairCost(ItemStack stack) {
		if(stack.isEmpty() && stack.hasTagCompound()) {
			stack.getTagCompound().removeTag("RepairCost");
		}
	}
}
