package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.common.RTConfig;
import com.therandomlabs.randomtweaks.common.RandomTweaks;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public final class SoundHandler {
	private SoundHandler() {}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onEvent(PlaySoundEvent event) {
		if(RTConfig.client.disableWitherSpawnSound &&
				event.getName().equals("entity.wither.spawn")) {
			event.setResultSound(null);
		}

		if(RTConfig.client.disableEnderDragonDeathSound &&
				event.getName().equals("entity.enderdragon.death")) {
			event.setResultSound(null);
		}
	}
}
