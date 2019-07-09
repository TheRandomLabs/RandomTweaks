package com.therandomlabs.randomtweaks.common;

import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ArrowImpactHandler {
	@SubscribeEvent
	public static void onArrowImpact(ProjectileImpactEvent.Arrow event) {
		if(!RTConfig.Misc.burningArrowsSetBlocksOnFire && !RTConfig.Misc.pickUpSkeletonArrows) {
			return;
		}

		final EntityArrow arrow = event.getArrow();
		final World world = arrow.getEntityWorld();

		if(world.isRemote) {
			return;
		}

		if(RTConfig.Misc.pickUpSkeletonArrows && arrow.shootingEntity instanceof EntitySkeleton &&
				arrow.pickupStatus == EntityArrow.PickupStatus.DISALLOWED) {
			arrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
		}

		if(!RTConfig.Misc.burningArrowsSetBlocksOnFire || !arrow.isBurning()) {
			return;
		}

		final Vec3d start = arrow.getPositionVector();
		final Vec3d end = start.add(arrow.motionX, arrow.motionY, arrow.motionZ);

		final RayTraceResult result = world.rayTraceBlocks(start, end, false, true, false);

		if(result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
			final BlockPos firePos = result.getBlockPos().up();

			if(world.isAirBlock(firePos)) {
				//11 = NOTIFY_NEIGHBORS | SEND_TO_CLIENTS | RERENDER_MAIN_THREAD
				//Taken from ItemFlintAndSteel#onItemUse
				world.setBlockState(firePos, Blocks.FIRE.getDefaultState(), 11);
			}
		}
	}
}
