package com.therandomlabs.randomtweaks.common;

import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RandomTweaks.MOD_ID)
public final class SquidHandler {
	public static final int RADIUS_LIMIT_DISABLED = 0;
	public static final int CHUNK_LIMIT_DISABLED = -1;
	public static final int SQUID_SPAWNING_DISABLED = 0;
	public static final int VANILLA_PACK_SIZE = 0;

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onLivingEntityPackSpawn(LivingPackSizeEvent event) {
		final Entity entity = event.getEntity();

		if(entity.getClass() == EntitySquid.class &&
				RTConfig.squids.maxPackSize != VANILLA_PACK_SIZE) {
			event.setMaxPackSize(RTConfig.squids.maxPackSize);
		}
	}

	@SubscribeEvent
	public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
		if(event.getEntity().getClass() == EntitySquid.class && !isInRadiusOfPlayer(event) ||
				tooManySquids(event)) {
			event.setResult(Event.Result.DENY);
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

		for(EntityPlayer player : event.getWorld().playerEntities) {
			if(player.getEntityBoundingBox().intersects(aabb)) {
				return true;
			}
		}

		return false;
	}

	public static boolean tooManySquids(LivingSpawnEvent.CheckSpawn event) {
		if(RTConfig.squids.chunkLimit == SQUID_SPAWNING_DISABLED) {
			return true;
		}

		if(RTConfig.squids.chunkLimit == CHUNK_LIMIT_DISABLED) {
			return false;
		}

		final World world = event.getWorld();
		final Chunk chunk = world.getChunk(event.getEntity().getPosition());

		int squids = 0;

		//Apparently a ConcurrentModificationException can occur here #38
		//so we iterate using i instead
		for(int i = 0; i < world.loadedEntityList.size(); i++) {
			final Entity entity = world.loadedEntityList.get(i);

			if(entity.getClass() != EntitySquid.class ||
					entity.chunkCoordX != chunk.x || entity.chunkCoordZ != chunk.z) {
				continue;
			}

			if(++squids >= RTConfig.squids.chunkLimit) {
				return true;
			}
		}

		return false;
	}
}
