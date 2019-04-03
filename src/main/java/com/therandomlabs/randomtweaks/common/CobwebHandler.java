package com.therandomlabs.randomtweaks.common;

import java.util.Random;
import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.ArrayUtils;

@Mod.EventBusSubscriber
public final class CobwebHandler {
	private static final Random random = new Random();

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		final World world = event.getWorld();
		final ItemStack stack = event.getItemStack();

		if(stack == null) {
			return;
		}

		final EntityPlayer player = event.getEntityPlayer();

		if(RTConfig.CobwebBurning.disableBurningIfSneaking && player.isSneaking()) {
			return;
		}

		final Item item = stack.getItem();

		if(!ArrayUtils.contains(RTConfig.CobwebBurning.items, item)) {
			return;
		}

		final BlockPos pos = event.getPos();

		if(world.getBlockState(pos).getBlock() != Blocks.WEB) {
			return;
		}

		world.setBlockToAir(pos);

		if(!player.capabilities.isCreativeMode) {
			if(stack.isItemStackDamageable()) {
				stack.damageItem(RTConfig.CobwebBurning.damageAmount, player);
			} else {
				stack.stackSize -= RTConfig.CobwebBurning.consumeAmount;
			}
		}

		if(RTConfig.CobwebBurning.burnSound != null) {
			world.playSound(
					player,
					pos,
					RTConfig.CobwebBurning.burnSound,
					SoundCategory.BLOCKS,
					1.0F,
					random.nextFloat() * 0.4F + 0.8F
			);
		}

		if(!world.isRemote) {
			//Values taken from
			//https://github.com/NerdHubMC/Realistic-Cobwebs/blob/master/src/main/java/mod/
			//upcraftlp/cobwebs/Main.java
			((WorldServer) world).spawnParticle(
					EnumParticleTypes.FLAME,
					pos.getX() + 0.5,
					pos.getY() + 0.5,
					pos.getZ() + 0.5,
					random.nextInt(40) + 7,
					random.nextDouble() * 0.5,
					random.nextDouble() * 0.5,
					random.nextDouble() * 0.5,
					0.005
			);
		}

		event.setCanceled(true);
	}
}
