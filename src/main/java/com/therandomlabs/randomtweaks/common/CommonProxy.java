package com.therandomlabs.randomtweaks.common;

import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	public static final Logger LOGGER = RandomTweaks.LOGGER;

	public void preInit(FMLPreInitializationEvent event) throws Exception {
		ConfigurationHandler.initialize(event);

		if(!ConfigurationHandler.readBoolean("moreRomanNumerals")) {
			return;
		}

		try {
			RTLanguageMap.replaceLanguageMaps();
		} catch(Exception ex) {
			LOGGER.error("Failed to replace LanguageMap instances - more Roman numerals " +
					"feature disabled");
		}
	}

	public void init(FMLInitializationEvent event) throws Exception {
		MinecraftForge.EVENT_BUS.register(new WorldCreateHandler());
		if(ConfigurationHandler.readBoolean("dontResetHungerOnRespawn")) {
			MinecraftForge.EVENT_BUS.register(new HungerHandler());
		}
	}
}
