package com.therandomlabs.randomtweaks.common;

import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class TrampleHandler {
	public enum Behavior {
		VANILLA("vanilla"),
		DONT_TRAMPLE_IF_FEATHER_FALLING("dontTrampleIfFeatherFalling"),
		DONT_TRAMPLE_IF_FEATHER_FALLING_OR_WET("dontTrampleIfFeatherFallingOrWet"),
		DONT_TRAMPLE_IF_WET("dontTrampleIfWet"),
		DONT_TRAMPLE("dontTrample");

		private final String translationKey;

		Behavior(String translationKey) {
			this.translationKey =
					"randomtweaks.config.misc.farmlandTrampleBehavior." + translationKey;
		}

		@Override
		public String toString() {
			return translationKey;
		}
	}

	@SubscribeEvent
	public static void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
		if (RTConfig.Misc.farmlandTrampleBehavior == Behavior.VANILLA) {
			return;
		}

		if (RTConfig.Misc.farmlandTrampleBehavior == Behavior.DONT_TRAMPLE) {
			event.setCanceled(true);
			return;
		}

		if (RTConfig.Misc.farmlandTrampleBehavior == Behavior.DONT_TRAMPLE_IF_WET ||
				RTConfig.Misc.farmlandTrampleBehavior ==
						Behavior.DONT_TRAMPLE_IF_FEATHER_FALLING_OR_WET) {
			final IBlockState state = event.getWorld().getBlockState(event.getPos());

			if (state.getBlock() instanceof BlockFarmland &&
					state.getValue(BlockFarmland.MOISTURE) == 7) {
				event.setCanceled(true);
				return;
			} else if (RTConfig.Misc.farmlandTrampleBehavior == Behavior.DONT_TRAMPLE_IF_WET) {
				return;
			}
		}

		final Iterable<ItemStack> armor = event.getEntity().getArmorInventoryList();

		for (ItemStack stack : armor) {
			final Item item = stack.getItem();

			if (!(item instanceof ItemArmor &&
					((ItemArmor) item).armorType == EntityEquipmentSlot.FEET)) {
				continue;
			}

			if(EnchantmentHelper.getEnchantmentLevel(Enchantments.FEATHER_FALLING, stack) > 0) {
				event.setCanceled(true);
				break;
			}
		}
	}
}
