package com.therandomlabs.randomtweaks.common;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = RandomTweaks.MODID, version = RandomTweaks.VERSION,
		acceptedMinecraftVersions = RandomTweaks.ACCEPTED_MINECRAFT_VERSIONS,
		acceptableRemoteVersions = "*")
public final class RandomTweaks {
	public static final String MODID = "randomtweaks";
	public static final String VERSION = "1.11.2-1.1.0.0";
	public static final String ACCEPTED_MINECRAFT_VERSIONS = "[1.10,1.12)";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	@Instance
	public static RandomTweaks instance;

	@SidedProxy(clientSide = "com.therandomlabs.randomtweaks.client.ClientProxy",
			serverSide = "com.therandomlabs.randomtweaks.common.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws Exception {
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) throws Exception {
		proxy.init(event);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) throws Exception {
		if(ConfigurationHandler.readBoolean("deletegameruleCommand")) {
			event.registerServerCommand(new CommandDeleteGamerule());
		}
		if(ConfigurationHandler.readBoolean("hungerCommand")) {
			event.registerServerCommand(new CommandHunger());
		}
	}

	public static String translate(String key, Object... args) {
		return new TextComponentTranslation(key, args).getFormattedText();
	}
}
