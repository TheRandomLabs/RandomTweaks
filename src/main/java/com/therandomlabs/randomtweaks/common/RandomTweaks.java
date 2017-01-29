package com.therandomlabs.randomtweaks.common;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = RandomTweaks.MODID, version = RandomTweaks.VERSION,
		acceptedMinecraftVersions = RandomTweaks.ACCEPTED_MINECRAFT_VERSIONS,
		acceptableRemoteVersions = "*")
public final class RandomTweaks {
	public static final String MODID = "randomtweaks";
	public static final String VERSION = "1.11.2-1.0.0.1";
	public static final String ACCEPTED_MINECRAFT_VERSIONS = "[1.10,1.12)";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	@SidedProxy(clientSide = "com.therandomlabs.randomtweaks.client.ClientProxy",
			serverSide = "com.therandomlabs.randomtweaks.common.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	public static Field findField(Class<?> clazz, Class<?> type) {
		for(Field field : clazz.getDeclaredFields()) {
			if(field.getType().isAssignableFrom(type)) {
				field.setAccessible(true);
				return field;
			}
		}
		return null;
	}

	public static List<Field> findFields(Class<?> clazz, Class<?> type) {
		final List<Field> fields = new ArrayList<>();
		for(Field field : clazz.getDeclaredFields()) {
			if(field.getType().isAssignableFrom(type)) {
				field.setAccessible(true);
				fields.add(field);
			}
		}
		return fields;
	}

	public static Field makeNotFinal(Field field) throws Exception {
		final Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		return field;
	}
}
