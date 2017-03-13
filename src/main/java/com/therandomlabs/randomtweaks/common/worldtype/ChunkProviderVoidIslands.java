package com.therandomlabs.randomtweaks.common.worldtype;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderOverworld;

public class ChunkProviderVoidIslands extends ChunkProviderOverworld {
	private final World world;
	private final Random random;

	public ChunkProviderVoidIslands(World world) {
		super(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(),
				WorldTypeRealistic.PRESET);
		this.world = world;
		this.random = new Random(world.getSeed());
	}

	@Override
	public Chunk provideChunk(int x, int z) {
	}
}
