package com.therandomlabs.randomtweaks.common;

import java.io.IOException;
import com.therandomlabs.randomtweaks.common.world.WorldGeneratorOceanFloor;
import com.therandomlabs.randomtweaks.common.world.WorldTypeRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) throws IOException {
		RTConfig.reloadConfig();

		if(RTConfig.general.moreRomanNumerals) {
			RTLanguageMap.replaceLanguageMaps();
		}
	}

	public void init(FMLInitializationEvent event) {
		WorldTypeRegistry.registerWorldTypes();

		if(RTConfig.oceanFloor.enabled && !Loader.isModLoaded("samsocean")) {
			GameRegistry.registerWorldGenerator(new WorldGeneratorOceanFloor(), 0);
		}
	}
}
