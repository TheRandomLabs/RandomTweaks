package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.common.RTConfig;
import com.therandomlabs.randomtweaks.common.RandomTweaks;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public class TimeOfDayHandler {
	@SubscribeEvent
	public static void onRenderTick(TickEvent.RenderTickEvent event) {
		if(!RTConfig.timeofday.enabled) {
			return;
		}

		final Minecraft mc = Minecraft.getMinecraft();

		if(mc.world == null) {
			return;
		}

		mc.fontRenderer.drawStringWithShadow("Test", 2.0F, 2.0F, 0xFFFFFF);
	}
}
