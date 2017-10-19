package com.therandomlabs.randomtweaks.common;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import com.therandomlabs.randomtweaks.common.worldtype.WorldTypeVoid;
import com.therandomlabs.randomtweaks.common.worldtype.WorldTypeVoidIslands;
import net.minecraft.block.Block;
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

@EventBusSubscriber(modid = RandomTweaks.MODID)
public final class WorldCreateHandler {
	@SubscribeEvent
	public static void onCreateSpawn(WorldEvent.CreateSpawnPosition event) throws Exception {
		final World world = event.getWorld();
		if(!world.isRemote && world.provider.getDimensionType() == DimensionType.OVERWORLD) {
			onWorldCreate(world);
		}
	}

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event) throws Exception {
		final World world = event.getWorld();
		if(!world.isRemote && world.provider.getDimensionType() == DimensionType.OVERWORLD &&
				world.getWorldType() instanceof WorldTypeVoidIslands) {
			onVoidIslandsWorldLoad(world);
		}
	}

	@SubscribeEvent
	public static void onPlayerLoad(PlayerEvent.LoadFromFile event) {
		final EntityPlayer player = event.getEntityPlayer();
		final World world = player.getEntityWorld();

		if(!new File(event.getPlayerDirectory(), event.getPlayerUUID() + ".dat").exists()) {
			if(world.getWorldType() instanceof WorldTypeVoid ||
					world.getWorldType() instanceof WorldTypeVoidIslands) {
				final BlockPos spawn = world.getSpawnPoint();
				player.setPosition(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5);
				player.setSpawnPoint(spawn, true);
			}
		}
	}

	private static void onWorldCreate(World world) throws Exception {
		if(world.getWorldType() instanceof WorldTypeVoid) {
			onVoidWorldCreate(world);
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
			gamerules.setOrCreateGameRule(entry.getKey(), entry.getValue());
		}
	}

	private static void onVoidWorldCreate(World world) {
		Block block = GameRegistry.findRegistry(Block.class).getValue(
				new ResourceLocation(RTConfig.world.voidWorldBlock));
		if(block == null) {
			block = Blocks.GLASS;
		}

		final BlockPos spawn = world.getSpawnPoint();
		final BlockPos newSpawn = new BlockPos(spawn.getX(),
				RTConfig.world.voidWorldYSpawn, spawn.getZ());

		world.setBlockState(newSpawn.down(1), block.getDefaultState());
		world.setSpawnPoint(newSpawn);
	}

	private static void onVoidIslandsWorldLoad(World world) {
		BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(0, 0, 0));

		if(world.isAirBlock(pos)) {
			for(int x = 1; x <= 16; x++) {
				boolean found = false;

				for(int z = 1; z <= 16; z++) {
					pos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
					if(world.isAirBlock(pos)) {
						found = true;
						break;
					}
				}

				if(found) {
					break;
				}
			}
		}

		world.setSpawnPoint(pos.add(0, 1, 0));
	}
}
