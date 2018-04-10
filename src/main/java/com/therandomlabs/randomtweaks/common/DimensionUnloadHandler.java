package com.therandomlabs.randomtweaks.common;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = RandomTweaks.MODID)
public final class DimensionUnloadHandler {
	private static int ticks;

	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if(RTConfig.general.dimensionUnloadCheckInterval == 0 ||
				event.phase != TickEvent.Phase.END) {
			return;
		}

		if(++ticks >= RTConfig.general.dimensionUnloadCheckInterval) {
			ticks = 0;

			for(int id : DimensionManager.getIDs()) {
				final WorldServer world = DimensionManager.getWorld(id);
				final ChunkProviderServer provider = world.getChunkProvider();

				if(id != DimensionType.OVERWORLD.getId() &&
						provider.getLoadedChunkCount() == 0 &&
						world.playerEntities.isEmpty() &&
						world.loadedEntityList.isEmpty() &&
						world.loadedTileEntityList.isEmpty()) {
					DimensionManager.unloadWorld(id);
				}
			}
		}
	}
}
