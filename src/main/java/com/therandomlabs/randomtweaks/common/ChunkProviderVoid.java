package com.therandomlabs.randomtweaks.common;

import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ChunkProviderVoid implements IChunkGenerator {
	private final World world;

	public ChunkProviderVoid(World world) {
		this.world = world;
	}

	@Override
	public Chunk provideChunk(int x, int z) {
		final Chunk chunk = new Chunk(world, x, z);

		if(!ConfigurationHandler.voidWorldTypeBiome.isEmpty()) {
			Biome biome = GameRegistry.findRegistry(Biome.class).getValue(
					new ResourceLocation(ConfigurationHandler.voidWorldTypeBiome));
			if(biome == null) {
				biome = Biomes.PLAINS;
			}

			final byte[] biomeArray = new byte[256];
			Arrays.fill(biomeArray, (byte) Biome.getIdForBiome(biome));
			chunk.setBiomeArray(biomeArray);
		}

		return chunk;
	}

	@Override
	public void populate(int x, int z) {}

	@Override
	public boolean generateStructures(Chunk chunkIn, int x, int z) {
		return false;
	}

	@Override
	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
		return null;
	}

	@Override
	public BlockPos getStrongholdGen(World world, String structureName, BlockPos position,
			boolean findUnexplored) {
		return null;
	}

	@Override
	public void recreateStructures(Chunk chunk, int x, int z) {}
}
