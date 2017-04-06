package com.therandomlabs.randomtweaks.common.worldtype;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkGenerator;

public class WorldTypeVoidIslands extends WorldType {
	public static final String NAME = "voidislands";

	public WorldTypeVoidIslands() {
		super(NAME);
	}

	@Override
	public boolean showWorldInfoNotice() {
		return true;
	}

	@Override
	public IChunkGenerator getChunkGenerator(World world, String generatorOptions) {
		return new ChunkProviderVoidIslands(world);
	}
}
