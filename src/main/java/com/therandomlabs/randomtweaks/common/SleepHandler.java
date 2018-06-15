package com.therandomlabs.randomtweaks.common;

import java.lang.reflect.Method;
import java.util.function.Function;
import com.therandomlabs.randomtweaks.base.RTConfig;
import com.therandomlabs.randomtweaks.base.RandomTweaks;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod.EventBusSubscriber(modid = RandomTweaks.MODID)
public final class SleepHandler {
	public static final Method SET_SIZE = ReflectionHelper.findMethod(Entity.class, "setSize",
			"func_70105_a", float.class, float.class);

	public static class MobFilter implements Function<EntityMob, Boolean> {
		@Override
		public Boolean apply(EntityMob mob) {
			return !(RTConfig.general.sleepTweaks && mob.hasCustomName());
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onSleep(PlayerSleepInBedEvent event) {
		if(!RTConfig.general.sleepTweaks) {
			return;
		}

		final EntityPlayer player = event.getEntityPlayer();
		final World world = player.getEntityWorld();

		if(world.isRemote) {
			return;
		}

		if(Loader.isModLoaded("comforts")) {
			for(StackTraceElement element : Thread.currentThread().getStackTrace()) {
				if(element.getClassName().equals("c4.comforts.common.items.ItemSleepingBag")) {
					return;
				}
			}
		}

		final BlockPos pos = event.getPos();

		IBlockState state;
		EnumFacing facing = null;

		try {
			state = world.isBlockLoaded(pos) ? world.getBlockState(pos) : null;

			if(state != null) {
				final ResourceLocation name = state.getBlock().getRegistryName();
				
				if(name != null && name.getResourceDomain().equals("comforts")) {
					return;
				}

				facing = state.getValue(BlockHorizontal.FACING);
			}
		} catch(IllegalArgumentException ex) {
			state = null;
			facing = null;
		}

		if(player.isPlayerSleeping() || !player.isEntityAlive()) {
			event.setResult(EntityPlayer.SleepResult.OTHER_PROBLEM);
			return;
		}

		if(!world.provider.isSurfaceWorld()) {
			event.setResult(EntityPlayer.SleepResult.NOT_POSSIBLE_HERE);
			return;
		}

		if(world.isDaytime()) {
			event.setResult(EntityPlayer.SleepResult.NOT_POSSIBLE_NOW);
			return;
		}

		if(!bedInRange(player, pos, facing)) {
			event.setResult(EntityPlayer.SleepResult.TOO_FAR_AWAY);
			return;
		}

		if(isMobInRange(player, world, pos)) {
			event.setResult(EntityPlayer.SleepResult.NOT_SAFE);
			return;
		}

		player.spawnShoulderEntities();

		if(player.isRiding()) {
			player.dismountRidingEntity();
		}

		try {
			SET_SIZE.invoke(player, 0.2F, 0.2F);
		} catch(Exception ex) {
			Utils.crashReport("Error while setting player size", ex);
		}

		if(state != null && state.getBlock().isBed(state, world, pos, player)) {
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

		event.setResult(EntityPlayer.SleepResult.OK);
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
				).grow(8.0, 5.0, 8.0),
				mob -> mob.isPreventingPlayerRest(player) && new MobFilter().apply(mob)
		).isEmpty();
	}

	public static void setRenderOffsetForSleep(EntityPlayer player, EnumFacing facing) {
		player.renderOffsetX = -1.8F * facing.getFrontOffsetX();
		player.renderOffsetZ = -1.8F * facing.getFrontOffsetZ();
	}
}
