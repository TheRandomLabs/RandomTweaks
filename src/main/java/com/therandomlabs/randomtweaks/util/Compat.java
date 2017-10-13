package com.therandomlabs.randomtweaks.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindFieldException;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;

public final class Compat {
	public static final String ACCEPTED_MINECRAFT_VERSIONS = "[1.12,1.13)";
	public static final boolean IS_ONE_POINT_TEN = false;
	public static final String CHICKEN_ENTITY_NAME = "chicken";

	public static interface ICompatChunkGenerator extends IChunkGenerator {}

	public static class ChunkGeneratorCompatOverworld extends ChunkGeneratorOverworld {
		public ChunkGeneratorCompatOverworld(World world, long seed, boolean mapFeaturesEnabled,
				String generatorOptions) {
			super(world, seed, mapFeaturesEnabled, generatorOptions);
		}
	}

	public static boolean isEmpty(ItemStack stack) {
		return stack.isEmpty();
	}

	public static int getStackSize(ItemStack stack) {
		return stack.getCount();
	}

	public static void setStackSize(ItemStack stack, int size) {
		stack.setCount(size);
	}

	public static void shrinkItemStack(ItemStack stack, int quantity) {
		stack.shrink(quantity);
	}

	public static void sendStatusMessage(EntityPlayer player, ITextComponent message)
			throws Exception {
		player.sendStatusMessage(message, true);
	}

	public static Field findField(Class<?> clazz, String... fieldNames) {
		try {
			for(String fieldName : fieldNames) {
				final Field field = clazz.getDeclaredField(fieldName);
				field.setAccessible(true);
				return field;
			}
		} catch(NoSuchFieldException ex) {
			return null;
		} catch(Exception ex) {
			throw new UnableToFindFieldException(fieldNames, ex);
		}

		return null;
	}

	public static Method findMethod(Class<?> clazz, String methodName, String obfuscatedName,
			Class<?>... parameterTypes) {
		final String nameToFind;
		if(obfuscatedName == null || Utils.isDeobfuscated()) {
			nameToFind = methodName;
		} else {
			nameToFind = obfuscatedName;
		}

		try {
			final Method method = clazz.getDeclaredMethod(nameToFind, parameterTypes);
			method.setAccessible(true);
			return method;
		} catch(NoSuchMethodException ex) {
			return null;
		} catch(Exception ex) {
			throw new UnableToFindMethodException(ex);
		}
	}

	public static void syncConfig(String modid, Config.Type type) {
		ConfigManager.sync(modid, type);
	}

	public static void clearChatMessages(GuiNewChat chat) {
		chat.clearChatMessages(false);
	}

	public static String buildString(String[] args, int startIndex) {
		final StringBuilder stringBuilder = new StringBuilder();
		for(int i = startIndex; i < args.length; i++) {
			if(i > startIndex) {
				stringBuilder.append(" ");
			}
			stringBuilder.append(args[i]);
		}
		return stringBuilder.toString();
	}

	public static void detectAndSendChanges(Container container) {
		container.detectAndSendChanges();
	}
}
