package com.therandomlabs.randomtweaks.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

//In an effort to avoid requiring dependencies
public final class Compat {
	private static final Field STACK_SIZE = isOnePointTen() ?
			ReflectionHelper.findField(ItemStack.class, "stackSize", "field_77994_a") : null;
	private static final Method SEND_STATUS_MESSAGE = isOnePointTen() ?
			ReflectionHelper.findMethod(EntityPlayer.class, null,
					new String[] {"addChatComponentMessage", "func_146105_b"},
					ITextComponent.class) : null;

	public static boolean isOnePointTen() {
		return !(MinecraftForge.MC_VERSION.equals("1.11") &&
				MinecraftForge.MC_VERSION.equals("1.11.2"));
	}

	public static boolean isEmpty(ItemStack stack) {
		return isOnePointTen() ? stack == null : stack.isEmpty();
	}

	public static void shrinkItemStack(ItemStack stack, int quantity)
			throws IllegalArgumentException, IllegalAccessException {
		if(isOnePointTen()) {
			STACK_SIZE.set(stack, quantity);
		} else {
			stack.shrink(quantity);
		}
	}

	public static void sendStatusMessage(EntityPlayer player, TextComponentTranslation message)
			throws Exception {
		if(isOnePointTen()) {
			SEND_STATUS_MESSAGE.invoke(player, message);
		} else {
			player.sendStatusMessage(message, true);
		}
	}
}
