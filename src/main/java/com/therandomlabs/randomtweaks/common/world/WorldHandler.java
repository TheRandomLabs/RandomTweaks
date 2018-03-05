package com.therandomlabs.randomtweaks.common.world;

import com.therandomlabs.randomtweaks.common.RTConfig;
import com.therandomlabs.randomtweaks.common.RandomTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import java.util.Map;
import java.util.Map.Entry;

@EventBusSubscriber(modid = RandomTweaks.MODID)
public final class WorldHandler {
	@SubscribeEvent
	public static void onCreateSpawn(WorldEvent.CreateSpawnPosition event) throws Exception {
		final World world = event.getWorld();
		if(!world.isRemote && world.provider.getDimensionType() == DimensionType.OVERWORLD) {
			initializeWorld(event, world);
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

		if(world.provider.getDimensionType() != DimensionType.OVERWORLD ||
				!(world.getWorldType() instanceof WorldTypeVoid)) {
			return;
		}

		BlockPos spawnPoint = player.getBedLocation(DimensionType.OVERWORLD.getId());
		boolean setWorldSpawn = false;
		if(spawnPoint == null) {
			spawnPoint = world.getSpawnPoint();
			setWorldSpawn = true;
		}

		final BlockPos topBlock = world.getTopSolidOrLiquidBlock(spawnPoint);
		IBlockState state = world.getBlockState(topBlock);
		if(!state.getMaterial().blocksMovement() || state.getBlock().isFoliage(world, topBlock)) {
			final BlockPos newSpawn = new BlockPos(0.5, RTConfig.world.voidWorldYSpawn, 0.5);

			player.setPosition(0.5, RTConfig.world.voidWorldYSpawn, 0.5);
			player.setSpawnPoint(newSpawn, true);

			if(setWorldSpawn) {
				world.setSpawnPoint(newSpawn);
			}

			final BlockPos spawnBlock = newSpawn.down();
			final IBlockState spawnBlockState = world.getBlockState(spawnBlock);

			if(!spawnBlockState.getMaterial().blocksMovement() ||
					spawnBlockState.getBlock().isFoliage(world, spawnBlock)) {
				Block block = GameRegistry.findRegistry(Block.class).getValue(
						new ResourceLocation(RTConfig.world.voidWorldBlock));
				if(block == null) {
					block = Blocks.GLASS;
				}

				world.setBlockState(new BlockPos(0, RTConfig.world.voidWorldYSpawn - 1, 0),
						block.getDefaultState());
			}
		}
	}

	private static void initializeWorld(WorldEvent.CreateSpawnPosition event,
			World world) throws Exception {
		if(world.getWorldType() instanceof WorldTypeVoid) {
			world.setSpawnPoint(new BlockPos(0.5, RTConfig.world.voidWorldYSpawn, 0.5));
			event.setCanceled(true);
		}

		final GameRules gamerules = world.getGameRules();
		final Map<String, String> defaultGamerules = RTConfig.getDefaultGamerules(
				world.getWorldInfo().getGameType().getID(), world.getWorldType().getName());

		if(defaultGamerules == null) {
			world.getMinecraftServer().sendMessage(
					new TextComponentTranslation("defaultGamerules.parseFailure"));
			return;
		}

		for(Entry<String, String> entry : defaultGamerules.entrySet()) {
			if(entry.getKey().equals("rtWorldBorderSize")) {
				try {
					world.getWorldBorder().setSize(Integer.parseInt(entry.getValue()));
					continue;
				} catch(NumberFormatException ex) {}
			}
			gamerules.setOrCreateGameRule(entry.getKey(), entry.getValue());
		}
	}
}
