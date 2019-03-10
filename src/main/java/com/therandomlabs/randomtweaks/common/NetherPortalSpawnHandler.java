package com.therandomlabs.randomtweaks.common;

import com.therandomlabs.randomportals.api.event.NetherPortalEvent;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class NetherPortalSpawnHandler {
	public static class RandomPortals {
		@SubscribeEvent
		public static void onPortalActivate(NetherPortalEvent.Activate event) {
			handle(event, event.getFrame().getWorld());
		}
	}

	public static class Vanilla {
		@SubscribeEvent
		public static void onPortalSpawn(BlockEvent.PortalSpawnEvent event) {
			handle(event, event.getWorld());
		}
	}

	private static void handle(Event event, World world) {
		final String name = RTConfig.GameRules.disableNetherPortalCreation;

		if(!name.isEmpty() && world.getGameRules().getBoolean(name)) {
			event.setCanceled(true);
		}
	}
}
