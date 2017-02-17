package com.therandomlabs.randomtweaks.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = RandomTweaks.MODID)
public final class SquidHandler {
	private static Chunk isInChunk;

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onLivingEntitySpawn(LivingSpawnEvent.CheckSpawn event) {
		if(event.getEntity().getClass() == EntitySquid.class) {
			if(!isInRadiusOfPlayer(event) || tooManySquids(event)) {
				event.setResult(Result.DENY);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingEntityPackSpawn(LivingPackSizeEvent event) {
		if(ConfigurationHandler.maxSquidPackSize != 0 &&
				event.getEntity().getClass() == EntitySquid.class) {
			event.setMaxPackSize(ConfigurationHandler.maxSquidPackSize);
		}
	}

	public static boolean isInRadiusOfPlayer(LivingSpawnEvent.CheckSpawn event) {
		final int radius = ConfigurationHandler.squidSpawnLimitRadius;
		if(radius == 0) { //Disabled
			return true;
		}
		return event.getWorld().getEntitiesWithinAABB(EntityPlayer.class,
				new AxisAlignedBB(event.getX(), event.getY(), event.getZ(), event.getX(),
						event.getY(), event.getZ()).expand(radius, radius, radius)).size() != 0;
	}

	public static boolean tooManySquids(LivingSpawnEvent.CheckSpawn event) {
		if(ConfigurationHandler.squidChunkLimit == 0) { //Squid spawning disabled
			return true;
		}
		if(ConfigurationHandler.squidChunkLimit == -1) { //Limit disabled
			return false;
		}

		isInChunk = event.getWorld().getChunkFromBlockCoords(event.getEntity().getPosition());
		return event.getWorld().getEntities(EntitySquid.class,
				SquidHandler::isInChunk).size() >= ConfigurationHandler.squidChunkLimit;
	}

	private static boolean isInChunk(Entity entity) {
		return entity.chunkCoordX == isInChunk.xPosition &&
				entity.chunkCoordZ == isInChunk.zPosition;
	}
}
