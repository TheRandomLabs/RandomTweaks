package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.common.RTConfig;
import com.therandomlabs.randomtweaks.common.RandomTweaks;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public class PotionShiftEvent {
	@SubscribeEvent
	public static void onPotionShiftEvent(GuiScreenEvent.PotionShiftEvent event) {
		if(RTConfig.client.disablePotionShift) {
			event.setCanceled(true);
		}
	}
}
