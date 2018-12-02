package com.therandomlabs.randomtweaks.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import com.therandomlabs.randomtweaks.common.RTLanguageMap;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public final class RTUtils {
	public static final IForgeRegistry<Block> BLOCK_REGISTRY =
			GameRegistry.findRegistry(Block.class);
	public static final IForgeRegistry<Biome> BIOME_REGISTRY =
			GameRegistry.findRegistry(Biome.class);

	public static String localize(String key, Object... args) {
		return RTLanguageMap.INSTANCE.translateKeyFormat(key, args);
	}

	public static Block getBlock(String blockName, Block defaultBlock) {
		final Block block = BLOCK_REGISTRY.getValue(new ResourceLocation(blockName));
		return block == null ? defaultBlock : block;
	}

	public static Biome getBiome(String biomeName, Biome defaultBiome) {
		final Biome biome = BIOME_REGISTRY.getValue(new ResourceLocation(biomeName));
		return biome == null ? defaultBiome : biome;
	}

	public static Field findField(Class<?> clazz, String... names) {
		for(Field field : clazz.getDeclaredFields()) {
			for(String name : names) {
				if(name.equals(field.getName())) {
					field.setAccessible(true);
					return field;
				}
			}
		}

		return null;
	}

	public static Method findMethod(Class<?> clazz, String name, String obfName,
			Class<?>... parameterTypes) {
		for(Method method : clazz.getDeclaredMethods()) {
			if((name.equals(method.getName()) || obfName.equals(method.getName())) &&
					Arrays.equals(method.getParameterTypes(), parameterTypes)) {
				method.setAccessible(true);
				return method;
			}
		}

		return null;
	}

	public static void crashReport(String message, Throwable throwable) {
		throw new ReportedException(new CrashReport(message, throwable));
	}
}
