package com.therandomlabs.randomtweaks.common;

import java.io.IOException;
import com.therandomlabs.randomtweaks.command.CommandRegistry;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = RandomTweaks.MODID, version = RandomTweaks.VERSION,
		acceptedMinecraftVersions = RandomTweaks.ACCEPTED_MINECRAFT_VERSIONS,
		acceptableRemoteVersions = RandomTweaks.ACCEPTABLE_REMOTE_VERSIONS,
		updateJSON = RandomTweaks.UPDATE_JSON,
		certificateFingerprint = RandomTweaks.CERTIFICATE_FINGERPRINT)
public final class RandomTweaks {
	public static final String MODID = "randomtweaks";
	public static final String VERSION = "@VERSION@";
	public static final String ACCEPTED_MINECRAFT_VERSIONS = "[1.12,1.13)";
	public static final String ACCEPTABLE_REMOTE_VERSIONS = "*";
	public static final String UPDATE_JSON =
			"https://raw.githubusercontent.com/TheRandomLabs/RandomTweaks/misc/versions.json";
	public static final String CERTIFICATE_FINGERPRINT = "@FINGERPRINT@";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	@SidedProxy(clientSide = "com.therandomlabs.randomtweaks.client.ClientProxy",
			serverSide = "com.therandomlabs.randomtweaks.common.CommonProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		try {
			proxy.preInit(event);
		} catch(IOException ex) {
			Utils.crashReport("Error occurred during PreInitialization", ex);
		}
	}

	@Mod.EventHandler
	public static void init(FMLInitializationEvent event) {
		proxy.init(event);
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
