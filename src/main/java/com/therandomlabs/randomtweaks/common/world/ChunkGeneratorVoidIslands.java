package com.therandomlabs.randomtweaks.common.world;

import java.util.Arrays;
import java.util.Random;
import com.therandomlabs.randomtweaks.common.RTConfig;
import com.therandomlabs.randomtweaks.util.Compat;
import net.minecraft.init.Biomes;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class ChunkGeneratorVoidIslands extends Compat.ChunkGeneratorCompatOverworld {
	private static String biomeName;
	private static Biome biome;

	private final World world;
	private final Random random;

	public ChunkGeneratorVoidIslands(World world) {
		super(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(),
				WorldTypeRealistic.PRESET);
		this.world = world;
		this.random = new Random(world.getSeed());
	}

	@Override
	public Chunk generateChunk(int x, int z) {
		if(random.nextInt(RTConfig.world.voidIslandsChunkRarity) == 0) {
			return super.generateChunk(x, z);
		}

		final Chunk chunk = new Chunk(world, x, z);

		final byte[] biomeArray = new byte[256];
		Arrays.fill(biomeArray, (byte) Biome.getIdForBiome(getBiome()));
		chunk.setBiomeArray(biomeArray);

		return chunk;
	}

	public static Biome getBiome() {
		if(biome == null || !RTConfig.world.voidIslandsWorldBiome.equals(biomeName)) {
			biomeName = RTConfig.world.voidIslandsWorldBiome;
			biome = Compat.getBiome(biomeName, Biomes.PLAINS);
		}

		return biome;
	}
}
