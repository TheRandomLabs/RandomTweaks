package com.therandomlabs.randomtweaks.common.world;

import java.util.Random;
import com.therandomlabs.randomtweaks.RTConfig;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGeneratorOceanFloor implements IWorldGenerator {
	private static final BlockMatcher GRAVEL = BlockMatcher.forBlock(Blocks.GRAVEL);

	private static final WorldGenMinable CLAY = new WorldGenMinable(
			Blocks.CLAY.getDefaultState(),
			RTConfig.oceanFloor.clayVeinSize,
			GRAVEL
	);

	private static final WorldGenMinable SAND = new WorldGenMinable(
			Blocks.SAND.getDefaultState(),
			RTConfig.oceanFloor.sandVeinSize,
			GRAVEL
	);

	private static final WorldGenMinable DIRT = new WorldGenMinable(
			Blocks.DIRT.getDefaultState(),
			RTConfig.oceanFloor.dirtVeinSize,
			GRAVEL
	);

	static {
		if(RTConfig.oceanFloor.minY > RTConfig.oceanFloor.maxY) {
			throw new IllegalArgumentException(
					"oceanFloor.minY must be lesser than or equal to oceanFloor.maxY"
			);
		}
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkGenerator generator, IChunkProvider provider) {
		if(world.provider.getDimensionType() == DimensionType.OVERWORLD) {
			generate(CLAY, RTConfig.oceanFloor.clayChance, random, chunkX, chunkZ, world);
			generate(SAND, RTConfig.oceanFloor.sandChance, random, chunkX, chunkZ, world);
			generate(DIRT, RTConfig.oceanFloor.dirtChance, random, chunkX, chunkZ, world);
		}
	}

	private void generate(WorldGenerator generator, int chance, Random random, int chunkX,
			int chunkZ, World world) {
		final int yDifference = RTConfig.oceanFloor.maxY - RTConfig.oceanFloor.minY;
		final int x = chunkX * 16;
		final int z = chunkZ * 16;

		for(int i = 0; i < chance; i++) {
			final BlockPos pos = new BlockPos(
					x + random.nextInt(16),
					RTConfig.oceanFloor.minY + random.nextInt(yDifference),
					z + random.nextInt(16)
			);

			final Biome biome = world.getBiome(pos);

			if(biome.getRegistryName().getPath().contains("ocean")) {
				generator.generate(world, random, pos);
			}
		}
	}
}
