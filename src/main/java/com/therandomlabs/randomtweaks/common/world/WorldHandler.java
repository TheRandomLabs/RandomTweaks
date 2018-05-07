package com.therandomlabs.randomtweaks.common.world;

import java.util.Map;
import java.util.Map.Entry;
import com.therandomlabs.randomtweaks.common.RTConfig;
import com.therandomlabs.randomtweaks.common.RandomTweaks;
import com.therandomlabs.randomtweaks.util.Compat;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = RandomTweaks.MODID)
public final class WorldHandler {
	@SubscribeEvent
	public static void onCreateSpawn(WorldEvent.CreateSpawnPosition event) throws Exception {
		final World world = event.getWorld();
		if(!world.isRemote && world.provider.getDimensionType() == DimensionType.OVERWORLD) {
			initializeWorld(world);
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

		if(world.provider.getDimensionType() == DimensionType.OVERWORLD &&
				(world.getWorldType() instanceof WorldTypeVoid ||
						world.getWorldType() instanceof WorldTypeVoidIslands)) {
			onPlayerSpawnInVoidWorld(player);
		}
	}

	private static void onPlayerSpawnInVoidWorld(EntityPlayer player) {
		final World world = player.getEntityWorld();

		BlockPos playerSpawnPoint = player.getBedLocation(DimensionType.OVERWORLD.getId());
		boolean shouldSetWorldSpawn = false;

		if(playerSpawnPoint == null) {
			playerSpawnPoint = world.getSpawnPoint();
			shouldSetWorldSpawn = true;
		}

		//Return if there is a block that the player can spawn on
		if(isSpawnable(world, world.getTopSolidOrLiquidBlock(playerSpawnPoint))) {
			return;
		}

		final int newSpawnY;
		if(world.getWorldType() instanceof WorldTypeVoid) {
			newSpawnY = RTConfig.world.voidWorldYSpawn;
		} else {
			final BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(0, 0, 0));
			newSpawnY = pos.getY() + 1;
		}

		final BlockPos newSpawn = new BlockPos(0.5, newSpawnY, 0.5);

		player.setPosition(0.5, newSpawnY, 0.5);
		player.setSpawnPoint(newSpawn, true);

		//If the player doesn't have a bed, i.e. this is the world spawn point
		if(shouldSetWorldSpawn) {
			world.setSpawnPoint(newSpawn);
		}

		if(!(world.getWorldType() instanceof WorldTypeVoid)) {
			return;
		}

		final BlockPos spawnBlock = new BlockPos(0, newSpawnY, 0);

		if(isSpawnable(world, spawnBlock)) {
			return;
		}

		final Block block = Compat.getBlock(RTConfig.world.voidWorldBlock, Blocks.GLASS);
		world.setBlockState(spawnBlock, block.getDefaultState());
	}

	private static boolean isSpawnable(World world, BlockPos pos) {
		final IBlockState state = world.getBlockState(pos);
		return state.getMaterial().blocksMovement() && !state.getBlock().isFoliage(world, pos);
	}

	private static void initializeWorld(World world) throws Exception {
		final GameRules gamerules = world.getGameRules();

		final int gamemode = world.getWorldInfo().getGameType().getID();
		final String type = world.getWorldType().getName();

		final Map<String, String> defaultGamerules = RTConfig.DefaultGamerules.get(gamemode, type);

		if(defaultGamerules == null) {
			failedToParseGamerules(world);
			return;
		}

		for(Entry<String, String> entry : defaultGamerules.entrySet()) {
			if(entry.getKey().equals("rtWorldBorderSize")) {
				try {
					world.getWorldBorder().setSize(Integer.parseInt(entry.getValue()));
				} catch(NumberFormatException ex) {
					ex.printStackTrace();
					failedToParseGamerules(world);
				}

				continue;
			}

			gamerules.setOrCreateGameRule(entry.getKey(), entry.getValue());
		}
	}

	private static void failedToParseGamerules(World world) {
		world.getMinecraftServer().sendMessage(
				new TextComponentTranslation("defaultGamerules.parseFailure"));
	}
}
