package com.therandomlabs.randomtweaks.common;

import java.util.List;
import com.therandomlabs.randomtweaks.common.world.WorldGeneratorOceanFloor;
import com.therandomlabs.randomtweaks.common.world.WorldTypeRegistry;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

public class CommonProxy {
	public static final Logger LOGGER = RandomTweaks.LOGGER;

	public void preInit(FMLPreInitializationEvent event) throws Exception {
		RTConfig.preInit();

		if(Utils.isDeobfuscated()) {
			//Defaults for testing
			RTConfig.timeofday.enabledByDefault = true;
			RTConfig.general.attackSpeed = 24.0;
			RTConfig.general.dropTESulfur = true;
			RTConfig.oceanFloor.enabled = true;
			RTConfig.respawn.deathPunishmentsIfKeepInventory = true;
			RTConfig.reloadConfig();
		}

		if(RTConfig.general.moreRomanNumerals) {
			try {
				RTLanguageMap.replaceLanguageMaps();
			} catch(Exception ex) {
				LOGGER.error("Failed to replace LanguageMap instances. More Roman numerals " +
						"feature disabled!", ex);
			}
		}
	}

	public void init(FMLInitializationEvent event) {
		WorldTypeRegistry.registerWorldTypes();

		if(RTConfig.oceanFloor.enabled) {
			GameRegistry.registerWorldGenerator(new WorldGeneratorOceanFloor(), 0);
		}

		if(Loader.isModLoaded("surge")) {
			try {
				disableSurgePigmanSleep();
			} catch(Exception ex) {
				LOGGER.error("Failed to disable Surge's pigman sleep fix feature!", ex);
			}
		}
	}

	private static void disableSurgePigmanSleep() throws Exception {
		final Class<?> clazz = Class.forName("org.epoxide.surge.features.FeatureManager");
		final List<?> features = (List<?>) clazz.getDeclaredField("FEATURES").get(null);

		for(Object feature : features) {
			final String className = feature.getClass().getName();
			if(className.equals("org.epoxide.surge.features.pigsleep.FeaturePigmanSleep")) {
				MinecraftForge.EVENT_BUS.unregister(feature);
				LOGGER.debug("Successfully disabled Surge's pigman sleep fix feature!");
				return;
			}
		}
	}
}
