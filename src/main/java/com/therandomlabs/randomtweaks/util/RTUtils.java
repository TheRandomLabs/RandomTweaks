package com.therandomlabs.randomtweaks.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Random;
import com.therandomlabs.randomtweaks.common.RTLanguageMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.ArrayUtils;

public final class RTUtils {
	public static String localize(String key, Object... args) {
		return RTLanguageMap.INSTANCE.translateKeyFormat(key, args);
	}

	public static Chunk createVoidChunk(World world, Random random, Biome biome,
			Biome[] biomeBlacklist, int x, int z) {
		final Chunk chunk = new Chunk(world, x, z);

		if(biome == null) {
			biome = world.getBiomeProvider().getBiome(new BlockPos(x * 16 + 8, 0, z * 16 + 8));

			while(ArrayUtils.contains(biomeBlacklist, biome)) {
				biome = Biome.REGISTRY.getRandomObject(random);
			}
		}

		final byte[] biomeArray = new byte[256];
		Arrays.fill(biomeArray, (byte) Biome.REGISTRY.getIDForObject(biome));
		chunk.setBiomeArray(biomeArray);

		return chunk;
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
			final String methodName = method.getName();

			if((name.equals(methodName) || obfName.equals(methodName)) &&
					Arrays.equals(method.getParameterTypes(), parameterTypes)) {
				method.setAccessible(true);
				return method;
			}
		}

		return null;
	}
}
