package com.therandomlabs.randomtweaks.common;

import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	public static final Logger LOGGER = RandomTweaks.LOGGER;

	public void preInit(FMLPreInitializationEvent event) {
		try {
			RTLanguageMap.replaceLanguageMaps();
		} catch(Exception ex) {
			LOGGER.error("Failed to replace LanguageMap instances - more Roman numerals " +
					"feature disabled");
		}
	}

	public void init(FMLInitializationEvent event) {}
}
