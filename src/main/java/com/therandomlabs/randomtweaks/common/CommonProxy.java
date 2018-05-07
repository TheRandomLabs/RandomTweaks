package com.therandomlabs.randomtweaks.common;

import com.therandomlabs.randomtweaks.common.world.WorldGeneratorOceanFloor;
import com.therandomlabs.randomtweaks.common.world.WorldTypeRegistry;
import com.therandomlabs.randomtweaks.util.Compat;
import com.therandomlabs.randomtweaks.util.Utils;
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
			RTConfig.client.stepup = true;
			RTConfig.commands.shortGamemodeCommands = true;
			RTConfig.timeofday.enabledByDefault = true;
			RTConfig.general.attackSpeed = 24.0;
			RTConfig.general.dropTESulfur = true;
			RTConfig.general.pickupSkeletonArrows = true;
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

		if(RTConfig.oceanFloor.enabled && !Loader.isModLoaded("samsocean")) {
			GameRegistry.registerWorldGenerator(new WorldGeneratorOceanFloor(), 0);
		}

		Compat.disableSurgePigmanSleep();
	}
}
