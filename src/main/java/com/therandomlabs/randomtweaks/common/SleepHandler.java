package com.therandomlabs.randomtweaks.common;

import java.lang.reflect.Method;
import java.util.function.Function;
import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.util.RTUtils;
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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RandomTweaks.MOD_ID)
public final class SleepHandler {
	public static class MobFilter implements Function<EntityMob, Boolean> {
		//Not actually a singleton
		public static final MobFilter INSTANCE = new MobFilter();

		@Override
		public Boolean apply(EntityMob mob) {
			return !(RTConfig.misc.sleepTweaks && mob.hasCustomName());
		}
	}

	public static final Method SET_SIZE = RTUtils.findMethod(
			Entity.class, "setSize", "func_70105_a", float.class, float.class
	);

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onSleep(PlayerSleepInBedEvent event) {
		if(!RTConfig.misc.sleepTweaks) {
			return;
		}

		final EntityPlayer player = event.getEntityPlayer();
		final World world = player.getEntityWorld();

		//RandomTweaks does not modify client-sided sleeping behavior
		if(world.isRemote) {
			return;
		}

		final BlockPos pos = event.getPos();

		IBlockState state;
		EnumFacing facing = null;

		try {
			state = world.isBlockLoaded(pos) ? world.getBlockState(pos) : null;

			if(state != null) {
				final ResourceLocation name = state.getBlock().getRegistryName();

				if(name != null && name.toString().startsWith("comforts:hammock")) {
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

		if(!player.bedInRange(pos, facing)) {
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
			RTUtils.crashReport("Error while setting player size", ex);
		}

		if(state != null && state.getBlock().isBed(state, world, pos, player)) {
			player.setRenderOffsetForSleep(facing);

			final float x = 0.5F + facing.getXOffset() * 0.4F;
			final float z = 0.5F + facing.getZOffset() * 0.4F;

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

		world.updateAllPlayersSleepingFlag();

		event.setResult(EntityPlayer.SleepResult.OK);
	}

	public static boolean isMobInRange(EntityPlayer player, World world, BlockPos position) {
		return !world.getEntitiesWithinAABB(
				EntityMob.class,
				new AxisAlignedBB(
						position.getX(),
						position.getY(),
						position.getZ(),
						position.getX(),
						position.getY(),
						position.getZ()
				).grow(8.0, 5.0, 8.0),
				mob -> mob.isPreventingPlayerRest(player) && MobFilter.INSTANCE.apply(mob)
		).isEmpty();
	}
}
