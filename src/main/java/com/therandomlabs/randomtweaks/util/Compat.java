package com.therandomlabs.randomtweaks.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;

//In an effort to avoid requiring dependencies
public final class Compat {
	public static final boolean IS_ONE_POINT_TEN = MinecraftForge.MC_VERSION.contains("10");
	private static final Field STACK_SIZE = IS_ONE_POINT_TEN ?
			ReflectionHelper.findField(ItemStack.class, "stackSize", "field_77994_a") : null;
	private static final Method SEND_STATUS_MESSAGE = IS_ONE_POINT_TEN ?
			findMethod(EntityPlayer.class,
					"addChatComponentMessage", "func_146105_b", ITextComponent.class) : null;

	public static boolean isEmpty(ItemStack stack) {
		if(IS_ONE_POINT_TEN) {
			return stack == null;
		}
		return stack.isEmpty();
	}

	public static int getStackSize(ItemStack stack) throws Exception {
		if(IS_ONE_POINT_TEN) {
			return (int) STACK_SIZE.get(stack);
		}
		return stack.getCount();
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
			STACK_SIZE.set(stack, quantity);
		} else {
			stack.shrink(quantity);
		}
	}

	public static void sendStatusMessage(EntityPlayer player, TextComponentTranslation message)
			throws Exception {
		if(IS_ONE_POINT_TEN) {
			SEND_STATUS_MESSAGE.invoke(player, message);
		} else {
			player.sendStatusMessage(message, true);
		}
	}

	public static Method findMethod(Class<?> clazz, String methodName, String obfuscatedName,
			Class<?>... parameterTypes) {
		/* TAKEN FROM ReflectionUtils */
		final String nameToFind;
		if(obfuscatedName == null ||
				(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
			nameToFind = methodName;
		} else {
			nameToFind = obfuscatedName;
		}

		try {
			final Method method = clazz.getDeclaredMethod(nameToFind, parameterTypes);
			method.setAccessible(true);
			return method;
		} catch(Exception ex) {
			throw new UnableToFindMethodException(ex);
		}
	}
}
