package com.therandomlabs.randomtweaks.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ReportedException;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;

//In an effort to avoid requiring dependencies
public final class Compat {
	public static final boolean IS_ONE_POINT_TEN = isOnePointTen();
	public static final String CHICKEN_ENTITY_NAME = IS_ONE_POINT_TEN ? "Chicken" : "chicken";

	private static final Field STACK_SIZE = IS_ONE_POINT_TEN ?
			ReflectionHelper.findField(ItemStack.class, "stackSize", "field_77994_a") : null;
	private static final Method ADD_CHAT_COMPONENT_MESSAGE = IS_ONE_POINT_TEN ? findMethod(
			EntityPlayer.class, "addChatComponentMessage", "func_146105_b", ITextComponent.class) :
				null;

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

	public static Method findMethod(Class<?> clazz, String methodName, String obfuscatedName,
			Class<?>... parameterTypes) {
		/* TAKEN FROM ReflectionUtils */
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
		} catch(Exception ex) {
			throw new UnableToFindMethodException(ex);
		}
	}

	private static boolean isOnePointTen() {
		try {
			return ((String) MinecraftForge.class.getDeclaredField("MC_VERSION").get(null)).
					startsWith("1.10");
		} catch(Exception ex) {
			throw new ReportedException(new CrashReport(
					"RandomTweaks could not get the current Minecraft version", ex));
		}
	}
}
