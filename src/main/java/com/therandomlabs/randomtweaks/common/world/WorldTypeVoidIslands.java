package com.therandomlabs.randomtweaks.common.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldTypeVoidIslands extends WorldType {
	public static final String NAME = "voidislands";

	public WorldTypeVoidIslands() {
		super(NAME);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasInfoNotice() {
		return true;
	}

	@Override
	public ChunkGeneratorVoidIslands getChunkGenerator(World world, String generatorOptions) {
		return new ChunkGeneratorVoidIslands(world);
	}
}
