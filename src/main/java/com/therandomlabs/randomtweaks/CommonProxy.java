package com.therandomlabs.randomtweaks;

import com.therandomlabs.randomlib.config.ConfigManager;
import com.therandomlabs.randomtweaks.common.NetherPortalSpawnHandler;
import com.therandomlabs.randomtweaks.common.RTFoodStats;
import com.therandomlabs.randomtweaks.common.RTLanguageMap;
import com.therandomlabs.randomtweaks.common.world.WorldGeneratorOceanFloor;
import com.therandomlabs.randomtweaks.common.world.WorldTypeRegistry;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
	public void preInit() {
		ConfigManager.register(RTConfig.class);
		RTLanguageMap.replaceLanguageMaps();
	}

	public void init() {
		WorldTypeRegistry.registerWorldTypes();

		if(RTConfig.OceanFloor.enabled && !RandomTweaks.OCEAN_FLOOR_LOADED) {
			GameRegistry.registerWorldGenerator(new WorldGeneratorOceanFloor(), 0);
		}

		if(RTConfig.Hunger.enabled && RandomTweaks.APPLECORE_LOADED) {
			MinecraftForge.EVENT_BUS.register(new RTFoodStats.AppleCoreEventHandler());
		}

		if(RandomTweaks.RANDOMPORTALS_LOADED) {
			MinecraftForge.EVENT_BUS.register(new NetherPortalSpawnHandler.RPOHandler());
		}
	}
}
