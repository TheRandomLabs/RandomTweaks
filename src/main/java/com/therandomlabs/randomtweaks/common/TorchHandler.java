package com.therandomlabs.randomtweaks.common;

import java.util.function.Predicate;
import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RandomTweaks.MOD_ID)
public final class TorchHandler {
	public enum Behavior {
		NEVER("never", player -> false),
		SNEAKING("sneaking", EntityPlayer::isSneaking),
		NOT_SNEAKING("notSneaking", player -> !player.isSneaking()),
		ALWAYS("always", player -> true);

		private final String translationKey;
		private final Predicate<EntityPlayer> predicate;

		Behavior(String translationKey, Predicate<EntityPlayer> predicate) {
			this.translationKey = translationKey;
			this.predicate = predicate;
		}

		@Override
		public String toString() {
			return "randomtweaks.config.misc.torchReorientationBehavior." + translationKey;
		}

		public boolean test(EntityPlayer player) {
			return predicate.test(player);
		}
	}

	public enum Orientation {
		NORTH(EnumFacing.NORTH),
		SOUTH(EnumFacing.SOUTH),
		EAST(EnumFacing.EAST),
		WEST(EnumFacing.WEST),
		UP(EnumFacing.UP);

		private final EnumFacing facing;

		Orientation(EnumFacing facing) {
			this.facing = facing;
		}

		public EnumFacing get() {
			return facing;
		}
	}

	private static EnumFacing[] TORCH_ORIENTATIONS =
			BlockTorch.FACING.getAllowedValues().toArray(new EnumFacing[0]);

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		if(!RTConfig.Misc.torchReorientationBehavior.test(event.getPlayer())) {
			return;
		}

		final World world = event.getWorld();
		final BlockPos pos = event.getPos();

		for(EnumFacing neighboringSide : TORCH_ORIENTATIONS) {
			final BlockPos torchPos = pos.offset(neighboringSide);
			final IBlockState torchState = world.getBlockState(torchPos);
			final Block block = torchState.getBlock();

			if(!(block instanceof BlockTorch) ||
					canPlaceAt(world, pos, torchPos, torchState.getValue(BlockTorch.FACING))) {
				continue;
			}

			for(EnumFacing orientation : RTConfig.Misc.torchOrientations) {
				if(canPlaceAt(world, pos, torchPos, orientation)) {
					world.setBlockState(
							torchPos, torchState.withProperty(BlockTorch.FACING, orientation)
					);
					break;
				}
			}
		}
	}

	//Taken and adapted from BlockTorch#canPlaceAt
	public static boolean canPlaceAt(
			World world, BlockPos brokenPos, BlockPos torchPos, EnumFacing orientation
	) {
		final BlockPos attachPos = torchPos.offset(orientation.getOpposite());

		if(brokenPos.equals(attachPos)) {
			return false;
		}

		final IBlockState attachState = world.getBlockState(attachPos);
		final Block attachBlock = attachState.getBlock();
		final BlockFaceShape attachBlockShape =
				attachState.getBlockFaceShape(world, attachPos, orientation);

		//orientation will never be EnumFacing.DOWN so we don't bother checking for it

		if(orientation == EnumFacing.UP) {
			return attachBlock.canPlaceTorchOnTop(attachState, world, torchPos);
		}

		return !Block.isExceptBlockForAttachWithPiston(attachBlock) &&
				attachBlockShape == BlockFaceShape.SOLID;
	}
}
