package com.therandomlabs.randomtweaks.base;

import com.therandomlabs.randomtweaks.common.command.CommandRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = RandomTweaks.MODID, version = RandomTweaks.VERSION,
		acceptedMinecraftVersions = RandomTweaks.ACCEPTED_MINECRAFT_VERSIONS,
		acceptableRemoteVersions = RandomTweaks.ACCEPTABLE_REMOTE_VERSIONS,
		dependencies = RandomTweaks.DEPENDENCIES, updateJSON = RandomTweaks.UPDATE_JSON,
		certificateFingerprint = RandomTweaks.CERTIFICATE_FINGERPRINT)
public final class RandomTweaks {
	public static final String MODID = "randomtweaks";
	public static final String VERSION = "@VERSION@";
	public static final String ACCEPTED_MINECRAFT_VERSIONS = "[1.12.2,1.13)";
	public static final String ACCEPTABLE_REMOTE_VERSIONS = "*";
	public static final String DEPENDENCIES = "required-after:forge@[14.23.4.2718,);";
	public static final String UPDATE_JSON =
			"https://raw.githubusercontent.com/TheRandomLabs/RandomTweaks/misc/versions.json";
	public static final String CERTIFICATE_FINGERPRINT = "@FINGERPRINT@";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	@SidedProxy(clientSide = "com.therandomlabs.randomtweaks.base.ClientProxy",
			serverSide = "com.therandomlabs.randomtweaks.base.ServerProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public static void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@Mod.EventHandler
	public static void serverAboutToStart(FMLServerAboutToStartEvent event) {
		ServerProxy.server = event.getServer();
	}

	@Mod.EventHandler
	public static void serverStarting(FMLServerStartingEvent event) {
		CommandRegistry.register(event);
	}

	@Mod.EventHandler
	public static void serverStarted(FMLServerStartedEvent event) {
		CommandRegistry.serverStarted(event);
	}
}
