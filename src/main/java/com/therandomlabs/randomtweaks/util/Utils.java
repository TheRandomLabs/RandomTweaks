package com.therandomlabs.randomtweaks.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.registries.IForgeRegistry;

public final class Utils {
	public static final IForgeRegistry<Block> BLOCK_REGISTRY =
			GameRegistry.findRegistry(Block.class);
	public static final IForgeRegistry<Biome> BIOME_REGISTRY =
			GameRegistry.findRegistry(Biome.class);

	public static final Method SET_SIZE = ReflectionHelper.findMethod(
			Entity.class, "setSize", "func_70105_a", float.class, float.class);
	public static final Field SATURATION_LEVEL =
			ReflectionHelper.findField(FoodStats.class, "foodSaturationLevel", "field_75125_b");

	public static String localize(String key, Object... args) {
		return new TextComponentTranslation(key, args).getFormattedText();
	}

	public static float getMaxHealth(EntityLivingBase entity) {
		return (float) entity.getEntityAttribute(
				SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
	}

	public static void setSize(Entity entity, float width, float height)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SET_SIZE.invoke(entity, width, height);
	}

	public static void setSaturation(FoodStats stats, float saturation) throws Exception {
		SATURATION_LEVEL.set(stats, saturation);
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
