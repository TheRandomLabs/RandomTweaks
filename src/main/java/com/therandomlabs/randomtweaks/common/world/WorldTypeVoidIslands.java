package com.therandomlabs.randomtweaks.common.world;

import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.world.World;

public class WorldTypeVoidIslands extends RTWorldType {
	public static final String NAME = "voidislands";

	public WorldTypeVoidIslands() {
		super(NAME);
	}

	@Override
	public boolean isEnabled() {
		return RTConfig.VoidIslandsWorld.enabled;
	}

	@Override
	public ChunkProviderVoidIslands getChunkGenerator(World world, String generatorOptions) {
		return new ChunkProviderVoidIslands(world);
	}
}
