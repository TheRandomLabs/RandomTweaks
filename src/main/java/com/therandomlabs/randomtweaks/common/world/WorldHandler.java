package com.therandomlabs.randomtweaks.common.world;

import java.util.Random;
import java.util.UUID;
import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RandomTweaks.MOD_ID)
public final class WorldHandler {
	private static final Random random = new Random();

	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event) {
		if(!RTConfig.World.fixDuplicateEntityUUIDs) {
			return;
		}

		final World world = event.getWorld();

		if(world.isRemote) {
			return;
		}

		final Chunk chunk = event.getChunk();
		final WorldServer serverWorld = (WorldServer) world;

		//Taken and adapted from:
		//https://minecraft.curseforge.com/projects/deuf-duplicate-entity-uuid-fix

		for(ClassInheritanceMultiMap<Entity> entities : chunk.getEntityLists()) {
			for(Entity entity : entities) {
				if(entity instanceof EntityPlayer) {
					continue;
				}

				final UUID uuid = entity.getUniqueID();
				final Entity entityFromUUID = serverWorld.getEntityFromUuid(uuid);

				if(entityFromUUID != null && entityFromUUID != entity) {
					UUID newUUID;

					do {
						newUUID = MathHelper.getRandomUUID(random);
					} while(serverWorld.getEntityFromUuid(newUUID) != null);

					if(RTConfig.World.logEntityUUIDReassignments) {
						RandomTweaks.LOGGER.info(
								"Changing UUID of entity {} from {} to {}",
								EntityList.getKey(entity), uuid, newUUID
						);
					}

					entity.setUniqueId(newUUID);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event) {
		final World world = event.getWorld();

		if(world.isRemote) {
			return;
		}

		final GameRules gameRules = world.getGameRules();

		final String netherPortalCreation = RTConfig.GameRules.disableNetherPortalCreation;

		if(!netherPortalCreation.isEmpty() && !gameRules.hasRule(netherPortalCreation)) {
			gameRules.setOrCreateGameRule(netherPortalCreation, "false");
		}

		final String[] damageMultipliers = {
				RTConfig.GameRules.drowningDamageMultiplier,
				RTConfig.GameRules.fallDamageMultiplier,
				RTConfig.GameRules.fireDamageMultiplier
		};

		for(String gameRule : damageMultipliers) {
			if(!gameRule.isEmpty() && !gameRules.hasRule(gameRule)) {
				gameRules.setOrCreateGameRule(gameRule, "1.0");
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.Clone event) {
		onPlayerSpawn(event.getEntityPlayer());
	}

	@SubscribeEvent
	public static void onPlayerLoad(PlayerEvent.LoadFromFile event) {
		onPlayerSpawn(event.getEntityPlayer());
	}

	private static void onPlayerSpawn(EntityPlayer player) {
		final World world = player.getEntityWorld();

		if(world.provider.getDimensionType() != DimensionType.OVERWORLD) {
			return;
		}

		final WorldType type = world.getWorldType();

		if(type instanceof WorldTypeVoid) {
			onPlayerSpawnInVoidWorld(player, false);
		} else if(type instanceof WorldTypeVoidIslands) {
			onPlayerSpawnInVoidWorld(player, true);
		}
	}

	private static void onPlayerSpawnInVoidWorld(EntityPlayer player, boolean voidIslands) {
		final World world = player.getEntityWorld();

		if(isSpawnable(world, world.getTopSolidOrLiquidBlock(player.getPosition()))) {
			return;
		}

		BlockPos playerSpawnPoint = player.getBedLocation();
		boolean usingWorldSpawn = false;

		if(playerSpawnPoint == null) {
			playerSpawnPoint = world.getSpawnPoint();
			usingWorldSpawn = true;
		}

		playerSpawnPoint = world.getTopSolidOrLiquidBlock(playerSpawnPoint).down();

		if(isSpawnable(world, playerSpawnPoint)) {
			return;
		}

		//The player doesn't have a viable spawn point, so we find or create one at (0, 0)

		final int newSpawnY;

		if(voidIslands) {
			final BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(0, 0, 0));
			newSpawnY = pos.getY() + 1;
		} else {
			newSpawnY = RTConfig.VoidWorld.ySpawn;
		}

		final BlockPos newSpawn = new BlockPos(0, newSpawnY, 0);

		player.setPosition(0.5, newSpawnY, 0.5);
		player.setSpawnPoint(newSpawn, true);

		//If this is the world spawn point and it is invalid
		if(usingWorldSpawn) {
			world.setSpawnPoint(newSpawn);
		}

		if(voidIslands) {
			return;
		}

		final BlockPos spawnBlock = new BlockPos(0, newSpawnY - 1, 0);

		if(isSpawnable(world, spawnBlock)) {
			return;
		}

		world.setBlockState(spawnBlock, RTConfig.VoidWorld.spawnBlockState);
	}

	private static boolean isSpawnable(World world, BlockPos pos) {
		final IBlockState state = world.getBlockState(pos);
		final Block block = state.getBlock();

		if(block instanceof BlockBed) {
			return BlockBed.getSafeExitLocation(world, pos, 0) != null;
		}

		return state.getMaterial().blocksMovement();
	}
}
