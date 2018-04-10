package com.therandomlabs.randomtweaks.common;

import com.therandomlabs.randomtweaks.util.Compat;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RandomTweaks.MODID)
public final class AnvilCostHandler {
	@SubscribeEvent
	public static void onAnvilUpdate(AnvilUpdateEvent event) {
		if(RTConfig.general.disableCumulativeAnvilCost) {
			removeRepairCost(event.getLeft());
			removeRepairCost(event.getRight());
		}
	}

	@SubscribeEvent
	public static void onAnvilRepair(AnvilRepairEvent event) {
		if(RTConfig.general.disableCumulativeAnvilCost) {
			removeRepairCost(event.getItemResult());
		}
	}

	public static void removeRepairCost(ItemStack stack) {
		if(Compat.isEmpty(stack) && stack.hasTagCompound()) {
			stack.getTagCompound().removeTag("RepairCost");
		}
	}
}
