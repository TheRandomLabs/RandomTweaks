package com.therandomlabs.randomtweaks.common;

import java.util.List;
import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RandomTweaks.MODID)
public final class SquidHandler {
	public static final int RADIUS_LIMIT_DISABLED = 0;
	public static final int CHUNK_LIMIT_DISABLED = -1;
	public static final int SQUID_SPAWNING_DISABLED = 0;
	public static final int VANILLA_PACK_SIZE = 0;

	public static void onSquidSpawn(LivingSpawnEvent.CheckSpawn event) {
		if(!isInRadiusOfPlayer(event) || tooManySquids(event)) {
			event.setResult(Event.Result.DENY);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onLivingEntityPackSpawn(LivingPackSizeEvent event) {
		final Entity entity = event.getEntity();

		if(!entity.getEntityWorld().isRemote && entity.getClass() == EntitySquid.class &&
				RTConfig.squids.maxPackSize != VANILLA_PACK_SIZE) {
			event.setMaxPackSize(RTConfig.squids.maxPackSize);
		}
	}

	public static boolean isInRadiusOfPlayer(LivingSpawnEvent.CheckSpawn event) {
		final int radius = RTConfig.squids.spawnRadiusLimit;

		if(radius == RADIUS_LIMIT_DISABLED) {
			return true;
		}

		final AxisAlignedBB aabb = new AxisAlignedBB(
				event.getX(),
				event.getY(),
				event.getZ(),
				event.getX(),
				event.getY(),
				event.getZ()
		).expand(radius, radius, radius);

		return !event.getWorld().getEntitiesWithinAABB(EntityPlayer.class, aabb).isEmpty();
	}

	public static boolean tooManySquids(LivingSpawnEvent.CheckSpawn event) {
		switch(RTConfig.squids.chunkLimit) {
			case SQUID_SPAWNING_DISABLED:
				return true;
			case CHUNK_LIMIT_DISABLED:
				return false;
			default:
				final List<?> entities = event.getWorld().getEntities(
						EntitySquid.class,
						entity -> isInChunk(entity, event)
				);

				return entities.size() >= RTConfig.squids.chunkLimit;
		}
	}

	private static boolean isInChunk(Entity entity, LivingSpawnEvent.CheckSpawn event) {
		final Chunk chunk = event.getWorld().getChunk(entity.getPosition());
		return entity.chunkCoordX == chunk.x && entity.chunkCoordZ == chunk.z;
	}
}
