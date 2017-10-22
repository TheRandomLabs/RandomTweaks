package com.therandomlabs.randomtweaks.common.world;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.therandomlabs.randomtweaks.common.RTConfig;
import com.therandomlabs.randomtweaks.util.Compat;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ChunkGeneratorVoid implements Compat.ICompatChunkGenerator {
	private final World world;

	public ChunkGeneratorVoid(World world) {
		this.world = world;
	}

	@Override
	public Chunk generateChunk(int x, int z) {
		final Chunk chunk = new Chunk(world, x, z);

		if(!RTConfig.world.voidWorldBiome.isEmpty()) {
			Biome biome = GameRegistry.findRegistry(Biome.class).getValue(
					new ResourceLocation(RTConfig.world.voidWorldBiome));
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
		return Collections.emptyList();
	}

	@Override
	public BlockPos getNearestStructurePos(World world, String structureName, BlockPos position,
			boolean findUnexplored) {
		return null;
	}

	@Override
	public void recreateStructures(Chunk chunk, int x, int z) {}

	@Override
	public boolean isInsideStructure(World world, String structureName, BlockPos pos) {
		return false;
	}
}
