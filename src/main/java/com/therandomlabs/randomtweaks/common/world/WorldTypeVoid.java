package com.therandomlabs.randomtweaks.common.world;

import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.world.World;

public class WorldTypeVoid extends RTWorldType {
	public static final String NAME = "void";

	public WorldTypeVoid() {
		super(NAME);
	}

	@Override
	public boolean isEnabled() {
		return RTConfig.VoidWorld.enabled;
	}

	@Override
	public ChunkGeneratorVoid getChunkGenerator(World world, String generatorOptions) {
		return new ChunkGeneratorVoid(world);
	}
}
