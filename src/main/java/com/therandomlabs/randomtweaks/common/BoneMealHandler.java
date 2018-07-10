package com.therandomlabs.randomtweaks.common;

import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockReed;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RandomTweaks.MODID)
public final class BoneMealHandler {
	@SubscribeEvent
	public static void onBoneMeal(BonemealEvent event) {
		if(RandomTweaks.EXPANDED_BONEMEAL_LOADED) {
			return;
		}

		final World world = event.getWorld();

		if(world.isRemote) {
			return;
		}

		final IBlockState state = event.getBlock();
		final Block block = state.getBlock();
		final BlockPos pos = event.getPos();

		if(block == Blocks.CACTUS) {
			if(RTConfig.boneMeal.cacti != 0 &&
					grow(world, block, pos, BlockCactus.AGE, RTConfig.boneMeal.cacti)) {
				event.setResult(Event.Result.ALLOW);
			}
		} else if(block == Blocks.REEDS) {
			if(RTConfig.boneMeal.sugarCanes != 0 &&
					grow(world, block, pos, BlockReed.AGE, RTConfig.boneMeal.sugarCanes)) {
				event.setResult(Event.Result.ALLOW);
			}
		} else if(block == Blocks.NETHER_WART) {
			if(RTConfig.boneMeal.netherWarts) {
				final int age = state.getValue(BlockNetherWart.AGE);

				if(age < 3) {
					world.setBlockState(pos, state.withProperty(BlockNetherWart.AGE, age + 1), 2);
					event.setResult(Event.Result.ALLOW);
				}
			}
		}
	}

	public static boolean grow(World world, Block block, BlockPos pos, PropertyInteger ageProperty,
			int stages) {
		BlockPos tempPos;
		int i = 0;

		while(world.getBlockState((tempPos = pos.down())).getBlock() == block) {
			pos = tempPos;

			if(++i > 1) {
				return false;
			}
		}

		i = 0;

		while(world.getBlockState((tempPos = pos.up())).getBlock() == block) {
			pos = tempPos;

			if(++i > 1) {
				return false;
			}
		}

		IBlockState state = world.getBlockState(pos);

		final int originalStage = state.getValue(ageProperty);
		int newStage = originalStage + stages - 1;

		if(newStage > 15) {
			newStage = 15;
		}

		if(originalStage == newStage) {
			return false;
		}

		state = state.withProperty(ageProperty, newStage);

		world.setBlockState(pos, state, 4);
		block.updateTick(world, pos, state, world.rand);

		return true;
	}
}
