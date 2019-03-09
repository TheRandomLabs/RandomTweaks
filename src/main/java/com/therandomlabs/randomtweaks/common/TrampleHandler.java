package com.therandomlabs.randomtweaks.common;

import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RandomTweaks.MOD_ID)
public final class TrampleHandler {
	public enum Behavior {
		VANILLA("vanilla"),
		DONT_TRAMPLE_IF_FEATHER_FALLING("dontTrampleIfFeatherFalling"),
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
		if(RTConfig.Misc.farmlandTrampleBehavior == Behavior.VANILLA) {
			return;
		}

		if(RTConfig.Misc.farmlandTrampleBehavior == Behavior.DONT_TRAMPLE) {
			event.setCanceled(true);
			return;
		}

		final Iterable<ItemStack> armor = event.getEntity().getArmorInventoryList();

		for(ItemStack stack : armor) {
			final Item item = stack.getItem();

			if(!(item instanceof ItemArmor &&
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
