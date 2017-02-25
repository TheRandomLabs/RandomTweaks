package com.therandomlabs.randomtweaks.common;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@EventBusSubscriber(modid = RandomTweaks.MODID)
public final class SleepHandler {
	public static final Field ANGER_TARGET_UUID = ReflectionHelper.findField(EntityPigZombie.class,
			"angerTargetUUID", "field_175459_bn");
	public static final Field SLEEPING = ReflectionHelper.findField(EntityPlayer.class,
			"sleeping", "field_71083_bS");
	public static final Field SLEEP_TIMER = ReflectionHelper.findField(EntityPlayer.class,
			"sleepTimer", "field_71076_b");

	//Lowest so this executes last (for compatibility with mods like Surge)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onSleep(PlayerSleepInBedEvent event) throws Exception {
		if(!ConfigurationHandler.sleepTweaks) {
			return;
		}

		final EntityPlayer player = event.getEntityPlayer();
		final EnumFacing facing = (EnumFacing) player.getEntityWorld().
				getBlockState(event.getPos()).getValue(BlockHorizontal.FACING);

		if(!player.getEntityWorld().isRemote) {
			if(player.isPlayerSleeping() || !player.isEntityAlive()) {
				event.setResult(SleepResult.OTHER_PROBLEM);
				return;
			}

			if(!player.getEntityWorld().provider.isSurfaceWorld()) {
				event.setResult(SleepResult.NOT_POSSIBLE_HERE);
				return;
			}

			if(player.getEntityWorld().isDaytime()) {
				event.setResult(SleepResult.NOT_POSSIBLE_NOW);
				return;
			}

			if(!bedInRange(player, event.getPos(), facing)) {
				event.setResult(SleepResult.TOO_FAR_AWAY);
				return;
			}

			for(EntityMob mob : getMobsInRange(player.getEntityWorld(), event.getPos())) {
				if(mob.hasCustomName() || !isPigZombieAngryAt(mob, player)) {
					continue;
				}

				event.setResult(SleepResult.NOT_SAFE);
				return;
			}
		}

		if(player.isRiding()) {
			player.dismountRidingEntity();
		}

		Utils.setSize(player, 0.2F, 0.2F);

		IBlockState state = null;

		if(player.getEntityWorld().isBlockLoaded(event.getPos())) {
			state = player.getEntityWorld().getBlockState(event.getPos());
		}

		if(state != null &&
				state.getBlock().isBed(state, player.getEntityWorld(), event.getPos(), player)) {
			setRenderOffsetForSleep(player, facing);

			final float x = 0.5F + (float) facing.getFrontOffsetX() * 0.4F;
			final float z = 0.5F + (float) facing.getFrontOffsetZ() * 0.4F;

			player.setPosition(
					(double) ((float) event.getPos().getX() + x),
					(double) ((float) event.getPos().getY() + 0.6875F),
					(double) ((float) event.getPos().getZ() + z)
			);
		} else {
			player.setPosition(
					(double) ((float) event.getPos().getX() + 0.5F),
					(double) ((float) event.getPos().getY() + 0.6875F),
					(double) ((float) event.getPos().getZ() + 0.5F)
			);
		}

		SLEEPING.set(player, true);
		SLEEP_TIMER.set(player, 0);
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

		position = position.offset(facing.getOpposite());
		return Math.abs(player.posX - position.getX()) <= 3.0 &&
				Math.abs(player.posY - position.getY()) <= 2.0 &&
				Math.abs(player.posZ - position.getZ()) <= 3.0;
	}

	public static List<EntityMob> getMobsInRange(World world, BlockPos position) {
		return world.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB(
				position.getX(),
				position.getY(),
				position.getZ(),
				position.getX(),
				position.getY(),
				position.getZ()
		).expand(8.0, 5.0, 8.0));
	}

	//Returns true if pigman is not instanceof EntityPigZombie
	public static boolean isPigZombieAngryAt(EntityLivingBase pigman, EntityPlayer player)
			throws IllegalArgumentException, IllegalAccessException {
		return pigman instanceof EntityPigZombie ?
				(UUID) ANGER_TARGET_UUID.get(pigman) == player.getUniqueID() : true;
	}

	public static void setRenderOffsetForSleep(EntityPlayer player, EnumFacing facing) {
		player.renderOffsetX = -1.8F * (float) facing.getFrontOffsetX();
		player.renderOffsetZ = -1.8F * (float) facing.getFrontOffsetZ();
	}
}
