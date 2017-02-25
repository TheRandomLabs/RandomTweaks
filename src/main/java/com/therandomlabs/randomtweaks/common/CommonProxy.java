package com.therandomlabs.randomtweaks.common;

import org.apache.logging.log4j.Logger;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	public static final Logger LOGGER = RandomTweaks.LOGGER;

	public void preInit(FMLPreInitializationEvent event) throws Exception {
		ConfigurationHandler.initialize(event);

		if(ConfigurationHandler.moveBucketCreativeTab) {
			Items.BUCKET.setCreativeTab(CreativeTabs.TOOLS);
		}

		if(ConfigurationHandler.moreRomanNumerals) {
			try {
				RTLanguageMap.replaceLanguageMaps();
			} catch(Exception ex) {
				LOGGER.error("Failed to replace LanguageMap instances - more Roman numerals " +
						"feature disabled", ex);
			}
		}
	}

	public void init(FMLInitializationEvent event) {}
}
