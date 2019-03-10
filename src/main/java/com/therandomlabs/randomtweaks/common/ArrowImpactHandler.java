package com.therandomlabs.randomtweaks.common;

import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ArrowImpactHandler {
	@SubscribeEvent
	public static void onArrowImpact(ProjectileImpactEvent.Arrow event) {
		if(!RTConfig.Misc.pickUpSkeletonArrows) {
			return;
		}

		final EntityArrow arrow = event.getArrow();

		if(!arrow.getEntityWorld().isRemote && arrow.shootingEntity instanceof EntitySkeleton &&
				arrow.pickupStatus == EntityArrow.PickupStatus.DISALLOWED) {
			arrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
		}
	}
}
