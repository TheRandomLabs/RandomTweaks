package com.therandomlabs.randomtweaks.common;

import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.projectile.EntityArrow;

public final class ArrowSpawnHandler {
	public static void onArrowImpact(EntityArrow arrow) {
		if(RTConfig.general.pickupSkeletonArrows &&
				arrow.shootingEntity instanceof EntitySkeleton &&
				arrow.pickupStatus == EntityArrow.PickupStatus.DISALLOWED) {
			arrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
		}
	}
}
