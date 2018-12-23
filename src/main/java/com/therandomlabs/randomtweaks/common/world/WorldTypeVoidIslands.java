package com.therandomlabs.randomtweaks.common.world;

import com.therandomlabs.randomtweaks.RTConfig;
import net.minecraft.world.World;

public class WorldTypeVoidIslands extends RTWorldType {
	public static final String NAME = "voidislands";

	public WorldTypeVoidIslands() {
		super(NAME);
	}

	@Override
	public boolean isEnabled() {
		return RTConfig.voidIslandsWorld.enabled;
	}

	@Override
	public ChunkGeneratorVoidIslands getChunkGenerator(World world, String generatorOptions) {
		return new ChunkGeneratorVoidIslands(world);
	}
}
