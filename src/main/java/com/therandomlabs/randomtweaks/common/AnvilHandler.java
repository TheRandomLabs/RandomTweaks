package com.therandomlabs.randomtweaks.common;

import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RandomTweaks.MODID)
public final class AnvilHandler {
	@SubscribeEvent
	public static void onAnvilUpdate(AnvilUpdateEvent event) {
		if(RTConfig.misc.disableCumulativeAnvilCost) {
			removeRepairCost(event.getLeft());
			removeRepairCost(event.getRight());
		}
	}

	@SubscribeEvent
	public static void onAnvilRepair(AnvilRepairEvent event) {
		if(RTConfig.misc.disableCumulativeAnvilCost) {
			removeRepairCost(event.getItemResult());
		}
	}

	public static void removeRepairCost(ItemStack stack) {
		if(!stack.isEmpty() && stack.hasTagCompound()) {
			stack.getTagCompound().removeTag("RepairCost");
		}
	}
}
