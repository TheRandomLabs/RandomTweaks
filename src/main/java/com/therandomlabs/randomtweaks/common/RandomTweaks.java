package com.therandomlabs.randomtweaks.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.therandomlabs.randomtweaks.server.CommandRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = RandomTweaks.MODID, version = RandomTweaks.VERSION,
		acceptedMinecraftVersions = RandomTweaks.ACCEPTED_MINECRAFT_VERSIONS,
		acceptableRemoteVersions = "*", updateJSON =
		"https://raw.githubusercontent.com/TheRandomLabs/RandomTweaks/master/versions.json")
public final class RandomTweaks {
	public static final String MODID = "randomtweaks";
	public static final String VERSION = "1.11.2-1.5.0.0";
	public static final String ACCEPTED_MINECRAFT_VERSIONS = "[1.10,1.12)";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	@SidedProxy(clientSide = "com.therandomlabs.randomtweaks.client.ClientProxy",
			serverSide = "com.therandomlabs.randomtweaks.common.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) throws Exception {
		proxy.preInit(event);
	}

	@EventHandler
	public static void init(FMLInitializationEvent event) throws Exception {
		proxy.init(event);
	}

	@EventHandler
	public static void serverStarting(FMLServerStartingEvent event) {
		CommandRegistry.register(event);
	}
}
