package com.therandomlabs.randomtweaks.common.world;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.util.RTUtils;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;

public class ChunkGeneratorVoid implements IChunkGenerator {
	private final World world;
	private final Random random;

	public ChunkGeneratorVoid(World world) {
		this.world = world;
		random = new Random(world.getSeed());
	}

	@Override
	public Chunk generateChunk(int x, int z) {
		return RTUtils.createVoidChunk(
				world, random, RTConfig.voidWorld.actualBiome,
				RTConfig.voidWorld.biomeBlacklist, x, z
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
		return RTConfig.voidWorld.mobSpawning ?
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
}
