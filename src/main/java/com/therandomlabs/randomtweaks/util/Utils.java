package com.therandomlabs.randomtweaks.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ReportedException;
import net.minecraft.util.text.TextComponentTranslation;

public final class Utils {
	public static final Method SET_SIZE = Compat.findMethod(Entity.class, "setSize", "func_70105_a",
			float.class, float.class);

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

	public static boolean isDeobfuscated() {
		return (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	}

	public static void crashReport(String message, Throwable throwable) {
		throw new ReportedException(new CrashReport(message, throwable));
	}
}
