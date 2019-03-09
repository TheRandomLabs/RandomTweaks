package com.therandomlabs.randomtweaks.common.world;

import java.util.Random;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGeneratorOverworld;

public class ChunkGeneratorVoidIslands extends ChunkGeneratorOverworld {
	public static final int ONLY_GENERATE_SPAWN_CHUNK = 1;

	private final World world;
	private final Random random;

	public ChunkGeneratorVoidIslands(World world) {
		super(
				world,
				world.getSeed(),
				world.getWorldInfo().isMapFeaturesEnabled(),
				WorldTypeRealistic.PRESET
		);

		this.world = world;
		this.random = new Random(world.getSeed());
	}

	@Override
	public Chunk generateChunk(int x, int z) {
		//The spawn chunk should always be generated
		if(x == 0 && z == 0) {
			return super.generateChunk(x, z);
		}

		//If the rarity is set to 1, only the spawn chunk should be generated
		if(RTConfig.VoidIslandsWorld.chunkRarity != ONLY_GENERATE_SPAWN_CHUNK &&
				random.nextInt(RTConfig.VoidIslandsWorld.chunkRarity) == 0) {
			return super.generateChunk(x, z);
		}

		return ChunkGeneratorVoid.createVoidChunk(
				world, random, RTConfig.VoidIslandsWorld.voidChunkBiome,
				RTConfig.VoidIslandsWorld.voidChunkRandomBiomeBlacklist, x, z
		);
	}
}
