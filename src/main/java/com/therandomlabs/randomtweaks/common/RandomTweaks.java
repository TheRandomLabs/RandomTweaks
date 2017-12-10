package com.therandomlabs.randomtweaks.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.therandomlabs.randomtweaks.server.CommandRegistry;
import com.therandomlabs.randomtweaks.util.Compat;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = RandomTweaks.MODID, version = RandomTweaks.VERSION,
		acceptedMinecraftVersions = Compat.ACCEPTED_MINECRAFT_VERSIONS,
		acceptableRemoteVersions = RandomTweaks.ACCEPTABLE_REMOTE_VEERSIONS,
		updateJSON = RandomTweaks.UPDATE_JSON,
		certificateFingerprint = RandomTweaks.CERTIFICATE_FINGERPRINT)
public final class RandomTweaks {
	public static final String MODID = "randomtweaks";
	public static final String VERSION = "@VERSION@";
	public static final String ACCEPTABLE_REMOTE_VEERSIONS = "*";
	public static final String UPDATE_JSON =
			"https://raw.githubusercontent.com/TheRandomLabs/RandomTweaks/misc/versions.json";
	public static final String CERTIFICATE_FINGERPRINT = "@FINGERPRINT@";

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

	@EventHandler
	public static void serverStarted(FMLServerStartedEvent event) {
		CommandRegistry.serverStarted(event);
	}
}
