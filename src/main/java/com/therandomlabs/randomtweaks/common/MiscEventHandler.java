package com.therandomlabs.randomtweaks.common;

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
		if(!RTConfig.general.pickupSkeletonArrows) {
			return;
		}

		final EntityArrow arrow = event.getArrow();

		if(arrow.shootingEntity instanceof EntitySkeleton &&
				arrow.pickupStatus == EntityArrow.PickupStatus.DISALLOWED) {
			arrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if(event.getEntity() instanceof EntityPlayer) {
			((EntityPlayer) event.getEntity()).
					getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).
					setBaseValue(RTConfig.general.attackSpeed);
		}
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
