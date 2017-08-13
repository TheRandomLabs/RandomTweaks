package com.therandomlabs.randomtweaks.common.worldtype;

import java.util.Arrays;
import java.util.Random;
import com.therandomlabs.randomtweaks.common.ConfigurationHandler;
import net.minecraft.init.Biomes;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGeneratorOverworld;

public class ChunkGeneratorVoidIslands extends ChunkGeneratorOverworld {
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
		if((x == 0 && z == 0) ||
				random.nextInt(ConfigurationHandler.voidIslandsWorldTypeChunkRarity) == 0) {
			return super.generateChunk(x, z);
		}

		final Chunk chunk = new Chunk(world, x, z);

		final byte[] biomeArray = new byte[256];
		Arrays.fill(biomeArray, (byte) Biome.getIdForBiome(Biomes.PLAINS));
		chunk.setBiomeArray(biomeArray);

		return chunk;
	}
}
