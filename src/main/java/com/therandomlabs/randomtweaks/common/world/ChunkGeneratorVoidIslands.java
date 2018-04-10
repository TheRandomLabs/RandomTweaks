package com.therandomlabs.randomtweaks.common.world;

import java.util.Arrays;
import java.util.Random;
import com.therandomlabs.randomtweaks.common.RTConfig;
import com.therandomlabs.randomtweaks.util.Compat;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class ChunkGeneratorVoidIslands extends Compat.ChunkGeneratorCompatOverworld {
	private static final IForgeRegistry<Biome> BIOME_REGISTRY =
			GameRegistry.findRegistry(Biome.class);

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
		if((x == 0 && z == 0) || random.nextInt(RTConfig.world.voidIslandsChunkRarity) == 0) {
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
			biome = BIOME_REGISTRY.getValue(new ResourceLocation(biomeName));

			if(biome == null) {
				biome = Biomes.PLAINS;
			}
		}

		return biome;
	}
}
