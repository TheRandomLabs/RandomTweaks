package com.therandomlabs.randomtweaks.common.worldtype;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldTypeVoid extends WorldType {
	public static final String NAME = "void";

	public WorldTypeVoid() {
		super(NAME);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasInfoNotice() {
		return true;
	}

	@Override
	public ChunkGeneratorVoid getChunkGenerator(World world, String generatorOptions) {
		return new ChunkGeneratorVoid(world);
	}
}
