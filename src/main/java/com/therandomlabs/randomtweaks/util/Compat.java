package com.therandomlabs.randomtweaks.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.ChunkProviderOverworld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindFieldException;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;

public final class Compat {
	public static final String ACCEPTED_MINECRAFT_VERSIONS = "[1.10,1.12)";
	public static final boolean IS_ONE_POINT_TEN = isOnePointTen();
	public static final String CHICKEN_ENTITY_NAME = IS_ONE_POINT_TEN ? "Chicken" : "chicken";

	private static final Field STACK_SIZE =
			findField(ItemStack.class, "stackSize", "field_77994_a");
	private static final Method ADD_CHAT_COMPONENT_MESSAGE =
			findMethod(EntityPlayer.class, "addChatComponentMessage", "func_146105_b",
					ITextComponent.class);
	private static final Method LOAD =
			findMethod(ConfigManager.class, "load", "load", String.class, Config.Type.class);
	private static final Method CLEAR_CHAT_MESSAGES =
			findMethod(GuiNewChat.class, "clearChatMessages", "func_146231_a");

	public static interface ICompatChunkGenerator extends IChunkGenerator {
		boolean isInsideStructure(World world, String structureName, BlockPos pos);
	}

	public static class ChunkGeneratorCompatOverworld extends ChunkProviderOverworld {
		public ChunkGeneratorCompatOverworld(World world, long seed, boolean mapFeaturesEnabled,
				String generatorOptions) {
			super(world, seed, mapFeaturesEnabled, generatorOptions);
		}
	}

	public static boolean isEmpty(ItemStack stack) {
		return IS_ONE_POINT_TEN ? stack == null : stack.isEmpty();
	}

	public static int getStackSize(ItemStack stack) throws Exception {
		return IS_ONE_POINT_TEN ? (int) STACK_SIZE.get(stack) : stack.getCount();
	}

	public static void setStackSize(ItemStack stack, int size) throws Exception {
		if(IS_ONE_POINT_TEN) {
			STACK_SIZE.set(stack, size);
		} else {
			stack.setCount(size);
		}
	}

	public static void shrinkItemStack(ItemStack stack, int quantity) throws Exception {
		if(IS_ONE_POINT_TEN) {
			STACK_SIZE.set(stack, (int) STACK_SIZE.get(stack) - quantity);
		} else {
			stack.shrink(quantity);
		}
	}

	public static void sendStatusMessage(EntityPlayer player, ITextComponent message)
			throws Exception {
		if(IS_ONE_POINT_TEN) {
			ADD_CHAT_COMPONENT_MESSAGE.invoke(player, message);
		} else {
			player.sendStatusMessage(message, true);
		}
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
		if(IS_ONE_POINT_TEN) {
			try {
				LOAD.invoke(null, modid, type);
			} catch(Exception ex) {
				Utils.crashReport("Could not sync config", ex);
			}
		} else {
			ConfigManager.sync(modid, type);
		}
	}

	public static void clearChatMessages(GuiNewChat chat) {
		if(IS_ONE_POINT_TEN) {
			try {
				CLEAR_CHAT_MESSAGES.invoke(chat);
			} catch(Exception ex) {
				Utils.crashReport("Could not clear chat mesages", ex);
			}
		} else {
			chat.clearChatMessages(false);
		}
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
		//container.detectAndSendChanges();
	}

	private static boolean isOnePointTen() {
		try {
			return ((String) MinecraftForge.class.getDeclaredField("MC_VERSION").get(null)).
					startsWith("1.10");
		} catch(Exception ex) {
			Utils.crashReport("RandomTweaks could not get the current Minecraft version", ex);
		}
		return false;
	}
}
