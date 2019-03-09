package com.therandomlabs.randomtweaks.common.world;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import org.apache.commons.lang3.ArrayUtils;

public class ChunkGeneratorVoid implements IChunkGenerator {
	private final World world;
	private final Random random;

	public ChunkGeneratorVoid(World world) {
		this.world = world;
		random = new Random(world.getSeed());
	}

	@Override
	public Chunk generateChunk(int x, int z) {
		return createVoidChunk(
				world, random, RTConfig.VoidWorld.biome, RTConfig.VoidWorld.randomBiomeBlacklist,
				x, z
		);
	}

	@Override
	public void populate(int x, int z) {}

	@Override
	public boolean generateStructures(Chunk chunk, int x, int z) {
		return false;
	}

	@Override
	public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType,
			BlockPos pos) {
		return RTConfig.VoidWorld.mobSpawning ?
				world.getBiome(pos).getSpawnableList(creatureType) : Collections.emptyList();
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

	public static Chunk createVoidChunk(World world, Random random, Biome biome,
			Biome[] biomeBlacklist, int x, int z) {
		final Chunk chunk = new Chunk(world, x, z);

		if(biome == null) {
			biome = world.getBiomeProvider().getBiome(new BlockPos(x * 16 + 8, 0, z * 16 + 8));

			while(ArrayUtils.contains(biomeBlacklist, biome)) {
				biome = Biome.REGISTRY.getRandomObject(random);
			}
		}

		final byte[] biomeArray = new byte[256];
		Arrays.fill(biomeArray, (byte) Biome.REGISTRY.getIDForObject(biome));
		chunk.setBiomeArray(biomeArray);

		return chunk;
	}
}
