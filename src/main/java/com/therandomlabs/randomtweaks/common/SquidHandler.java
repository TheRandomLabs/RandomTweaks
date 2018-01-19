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
	public static final int RADIUS_LIMIT_DISABLED = 0;
	public static final int CHUNK_LIMIT_DISABLED = -1;
	public static final int SQUID_SPAWNING_DISABLED = 0;
	public static final int VANILLA_PACK_SIZE = 0;

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onLivingEntitySpawn(LivingSpawnEvent.CheckSpawn event) {
		if(event.getEntity().getClass() == EntitySquid.class) {
			if(!isInRadiusOfPlayer(event) || tooManySquids(event)) {
				event.setResult(Result.DENY);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onLivingEntityPackSpawn(LivingPackSizeEvent event) {
		if(RTConfig.squids.maxPackSize != VANILLA_PACK_SIZE &&
				event.getEntity().getClass() == EntitySquid.class) {
			event.setMaxPackSize(RTConfig.squids.maxPackSize);
		}
	}

	public static boolean isInRadiusOfPlayer(LivingSpawnEvent.CheckSpawn event) {
		final int radius = RTConfig.squids.spawnRadiusLimit;

		if(radius == RADIUS_LIMIT_DISABLED) {
			return true;
		}

		return event.getWorld().getEntitiesWithinAABB(EntityPlayer.class,
				new AxisAlignedBB(event.getX(), event.getY(), event.getZ(), event.getX(),
						event.getY(), event.getZ()).expand(radius, radius, radius)).size() != 0;
	}

	public static boolean tooManySquids(LivingSpawnEvent.CheckSpawn event) {
		switch(RTConfig.squids.chunkLimit) {
		case SQUID_SPAWNING_DISABLED:
			return true;
		case CHUNK_LIMIT_DISABLED:
			return false;
		default:
			return event.getWorld().getEntities(EntitySquid.class,
					entity -> isInChunk(entity, event)).
					size() >= RTConfig.squids.chunkLimit;
		}
	}

	private static boolean isInChunk(Entity entity, LivingSpawnEvent.CheckSpawn event) {
		final Chunk chunk = event.getWorld().getChunkFromBlockCoords(entity.getPosition());
		return entity.chunkCoordX == chunk.x && entity.chunkCoordZ == chunk.z;
	}
}
