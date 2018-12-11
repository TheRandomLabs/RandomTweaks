package com.therandomlabs.randomtweaks;

import com.therandomlabs.randomtweaks.common.command.CommandRegistry;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
		modid = RandomTweaks.MOD_ID, version = RandomTweaks.VERSION,
		acceptedMinecraftVersions = RandomTweaks.ACCEPTED_MINECRAFT_VERSIONS,
		acceptableRemoteVersions = RandomTweaks.ACCEPTABLE_REMOTE_VERSIONS,
		dependencies = RandomTweaks.DEPENDENCIES, updateJSON = RandomTweaks.UPDATE_JSON,
		certificateFingerprint = RandomTweaks.CERTIFICATE_FINGERPRINT
)
public final class RandomTweaks {
	public static final String MOD_ID = "randomtweaks";
	public static final String VERSION = "@VERSION@";
	public static final String ACCEPTED_MINECRAFT_VERSIONS = "[1.12.2,1.13)";
	public static final String ACCEPTABLE_REMOTE_VERSIONS = "*";
	public static final String DEPENDENCIES =
			"required-after:forge@[14.23.4.2718,);after:comforts@[1.2.0,)";
	public static final String UPDATE_JSON =
			"https://raw.githubusercontent.com/TheRandomLabs/RandomTweaks/misc/versions.json";
	public static final String CERTIFICATE_FINGERPRINT = "@FINGERPRINT@";

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static final boolean APPLECORE_LOADED = Loader.isModLoaded("applecore");
	public static final boolean COLORFUL_SHEEP_LOADED = Loader.isModLoaded("colorful-sheep");
	public static final boolean DING_LOADED = Loader.isModLoaded("ding");
	public static final boolean DYNAMIC_SURROUNDINGS_LOADED = Loader.isModLoaded("dsurround");
	public static final boolean EXPANDED_BONEMEAL_LOADED = Loader.isModLoaded("expandedbonemeal");
	public static final boolean HEADCRUMBS_LOADED = Loader.isModLoaded("headcrumbs");
	public static final boolean HELPFIXER_LOADED = Loader.isModLoaded("helpfixer");
	public static final boolean PLAYERS_DROP_HEADS_LOADED = Loader.isModLoaded("playersdropheads");
	public static final boolean QUARK_LOADED = Loader.isModLoaded("quark");
	public static final boolean OCEAN_FLOOR_LOADED = Loader.isModLoaded("samsocean");
	public static final boolean RANDOMPATCHES_LOADED = Loader.isModLoaded("randompatches");
	public static final boolean RANDOMPORTALS_LOADED = Loader.isModLoaded("randomportals");
	public static final boolean VANILLATWEAKS_LOADED = Loader.isModLoaded("vanillatweaks");

	public static final boolean IS_DEOBFUSCATED =
			(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

	@SidedProxy(
			clientSide = "com.therandomlabs.randomtweaks.ClientProxy",
			serverSide = "com.therandomlabs.randomtweaks.CommonProxy"
	)
	public static CommonProxy proxy;

	@Mod.EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@Mod.EventHandler
	public static void init(FMLInitializationEvent event) {
		proxy.init();
	}

	@Mod.EventHandler
	public static void serverStarting(FMLServerStartingEvent event) {
		CommandRegistry.register(event);
	}

	@Mod.EventHandler
	public static void serverStarted(FMLServerStartedEvent event) {
		CommandRegistry.serverStarted();
	}
}
