package com.therandomlabs.randomtweaks.common;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = RandomTweaks.MODID)
public final class SleepHandler {
	@SubscribeEvent
	public static void onSleep(PlayerSleepInBedEvent event) {
		if(!RTConfig.general.sleepTweaks || Loader.isModLoaded("comforts")) {
			return;
		}

		final EntityPlayer player = event.getEntityPlayer();
		final World world = player.getEntityWorld();
		final BlockPos location = event.getPos();

		IBlockState state;
		EnumFacing facing;

		try {
			state = world.isBlockLoaded(location) ? world.getBlockState(location) : null;
			facing = state != null ? state.getValue(BlockHorizontal.FACING) : null;
		} catch(IllegalArgumentException ex) {
			state = null;
			facing = null;
		}

		if(!world.isRemote) {
			if(player.isPlayerSleeping() || !player.isEntityAlive()) {
				event.setResult(SleepResult.OTHER_PROBLEM);
				return;
			}

			if(!world.provider.isSurfaceWorld()) {
				event.setResult(SleepResult.NOT_POSSIBLE_HERE);
				return;
			}

			if(world.isDaytime()) {
				event.setResult(SleepResult.NOT_POSSIBLE_NOW);
				return;
			}

			if(!bedInRange(player, location, facing)) {
				event.setResult(SleepResult.TOO_FAR_AWAY);
				return;
			}

			if(isMobInRange(player, world, location)) {
				event.setResult(SleepResult.NOT_SAFE);
				return;
			}
		}

		player.spawnShoulderEntities();

		if(player.isRiding()) {
			player.dismountRidingEntity();
		}

		player.setSize(0.2F, 0.2F);

		if(state != null && state.getBlock().isBed(state, world, location, player)) {
			setRenderOffsetForSleep(player, facing);

			final float x = 0.5F + facing.getFrontOffsetX() * 0.4F;
			final float z = 0.5F + facing.getFrontOffsetZ() * 0.4F;

			player.setPosition(
					event.getPos().getX() + x,
					event.getPos().getY() + 0.6875F,
					event.getPos().getZ() + z
			);
		} else {
			player.setPosition(
					event.getPos().getX() + 0.5F,
					event.getPos().getY() + 0.6875F,
					event.getPos().getZ() + 0.5F
			);
		}

		player.sleeping = true;
		player.sleepTimer = 0;
		player.bedLocation = event.getPos();
		player.motionX = 0.0;
		player.motionY = 0.0;
		player.motionZ = 0.0;

		if(!player.getEntityWorld().isRemote) {
			player.getEntityWorld().updateAllPlayersSleepingFlag();
		}

		event.setResult(SleepResult.OK);
	}

	public static boolean bedInRange(EntityPlayer player, BlockPos position, EnumFacing facing) {
		if(Math.abs(player.posX - position.getX()) <= 3.0 &&
				Math.abs(player.posY - position.getY()) <= 2.0 &&
				Math.abs(player.posZ - position.getZ()) <= 3.0) {
			return true;
		}

		if(facing == null) {
			return false;
		}

		position = position.offset(facing.getOpposite());
		return Math.abs(player.posX - position.getX()) <= 3.0 &&
				Math.abs(player.posY - position.getY()) <= 2.0 &&
				Math.abs(player.posZ - position.getZ()) <= 3.0;
	}

	public static boolean isMobInRange(EntityPlayer player, World world, BlockPos position) {
		return !world.getEntitiesWithinAABB(EntityMob.class,
				new AxisAlignedBB(
						position.getX(),
						position.getY(),
						position.getZ(),
						position.getX(),
						position.getY(),
						position.getZ()
				).expand(8.0, 5.0, 8.0),
				mob -> mob.isPreventingPlayerRest(player) && !mob.hasCustomName()
		).isEmpty();
	}

	public static void setRenderOffsetForSleep(EntityPlayer player, EnumFacing facing) {
		player.renderOffsetX = -1.8F * facing.getFrontOffsetX();
		player.renderOffsetZ = -1.8F * facing.getFrontOffsetZ();
	}
}
