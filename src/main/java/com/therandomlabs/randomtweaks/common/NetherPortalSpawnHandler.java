package com.therandomlabs.randomtweaks.common;

import com.therandomlabs.randomportals.api.event.NetherPortalEvent;
import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RandomTweaks.MOD_ID)
public final class NetherPortalSpawnHandler {
	public static class RPOHandler {
		@SubscribeEvent
		public void onPortalActivate(NetherPortalEvent.Activate event) {
			handle(event, event.getFrame().getWorld());
		}
	}

	@SubscribeEvent
	public static void onPortalSpawn(BlockEvent.PortalSpawnEvent event) {
		handle(event, event.getWorld());
	}

	private static void handle(Event event, World world) {
		final String name = RTConfig.Misc.disableNetherPortalCreationGameRuleName;

		if(!name.isEmpty() && world.getGameRules().getBoolean(name)) {
			event.setCanceled(true);
		}
	}
}
