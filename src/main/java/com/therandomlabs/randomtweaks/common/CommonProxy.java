package com.therandomlabs.randomtweaks.common;

import java.util.List;
import org.apache.logging.log4j.Logger;
import com.therandomlabs.randomtweaks.common.world.WorldTypeRegistry;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	public static final Logger LOGGER = RandomTweaks.LOGGER;

	public void preInit(FMLPreInitializationEvent event) throws Exception {
		RTConfig.preInit();

		if(Utils.isDeobfuscated()) {
			RTConfig.timeofday.enabledByDefault = true;
			RTConfig.general.attackSpeed = 24.0;
			RTConfig.respawn.deathPunishmentsIfKeepInventory = true;
			RTConfig.reloadConfig();
		}

		if(RTConfig.general.moreRomanNumerals) {
			try {
				RTLanguageMap.replaceLanguageMaps();
			} catch(Exception ex) {
				LOGGER.error("Failed to replace LanguageMap instances - more Roman numerals " +
						"feature disabled", ex);
			}
		}
	}

	public void init(FMLInitializationEvent event) {
		WorldTypeRegistry.registerWorldTypes();

		if(Loader.isModLoaded("surge")) {
			try {
				final List<?> features =
						(List<?>) Class.forName("org.epoxide.surge.features.FeatureManager").
						getDeclaredField("FEATURES").get(null);
				for(Object feature : features) {
					if(feature.getClass().getName().equals(
							"org.epoxide.surge.features.pigsleep.FeaturePigmanSleep")) {
						MinecraftForge.EVENT_BUS.unregister(feature);
						LOGGER.info("Successfully disabled Surge's pigman sleep fix feature!");
						return;
					}
				}
			} catch(Exception ex) {
				LOGGER.error("Failed to disable Surge's pigman sleep fix feature", ex);
			}
		}
	}
}
