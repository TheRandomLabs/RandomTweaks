package com.therandomlabs.randomtweaks.util;

import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public final class Utils {
	public static final IForgeRegistry<Block> BLOCK_REGISTRY =
			GameRegistry.findRegistry(Block.class);
	public static final IForgeRegistry<Biome> BIOME_REGISTRY =
			GameRegistry.findRegistry(Biome.class);

	public static String localize(String key, Object... args) {
		return new TextComponentTranslation(key, args).getFormattedText();
	}

	public static Block getBlock(String blockName, Block defaultBlock) {
		final Block block = BLOCK_REGISTRY.getValue(new ResourceLocation(blockName));
		return block == null ? defaultBlock : block;
	}

	public static Biome getBiome(String biomeName, Biome defaultBiome) {
		final Biome biome = BIOME_REGISTRY.getValue(new ResourceLocation(biomeName));
		return biome == null ? defaultBiome : biome;
	}

	public static boolean isDeobfuscated() {
		return (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	}

	public static void crashReport(String message, Throwable throwable) {
		throw new ReportedException(new CrashReport(message, throwable));
	}
}
