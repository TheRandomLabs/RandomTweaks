package com.therandomlabs.randomtweaks;

import com.therandomlabs.randomlib.TRLUtils;
import com.therandomlabs.randomlib.config.ConfigManager;
import com.therandomlabs.randomtweaks.common.ArrowImpactHandler;
import com.therandomlabs.randomtweaks.common.NetherPortalSpawnHandler;
import com.therandomlabs.randomtweaks.common.RTFoodStats;
import com.therandomlabs.randomtweaks.common.RTLanguageMap;
import com.therandomlabs.randomtweaks.common.TrampleHandler;
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
		if("1.12.2".equals(TRLUtils.MC_VERSION)) {
			MinecraftForge.EVENT_BUS.register(ArrowImpactHandler.class);
			MinecraftForge.EVENT_BUS.register(TrampleHandler.class);

			if(RandomTweaks.RANDOMPORTALS_LOADED) {
				MinecraftForge.EVENT_BUS.register(NetherPortalSpawnHandler.RandomPortals.class);
			} else {
				MinecraftForge.EVENT_BUS.register(NetherPortalSpawnHandler.Vanilla.class);
			}
		}

		WorldTypeRegistry.registerWorldTypes();

		if(RTConfig.OceanFloor.enabled && !RandomTweaks.OCEAN_FLOOR_LOADED) {
			GameRegistry.registerWorldGenerator(new WorldGeneratorOceanFloor(), 0);
		}

		if(RTConfig.Hunger.enabled && RandomTweaks.APPLECORE_LOADED) {
			MinecraftForge.EVENT_BUS.register(RTFoodStats.AppleCoreEventHandler.class);
		}
	}
}
