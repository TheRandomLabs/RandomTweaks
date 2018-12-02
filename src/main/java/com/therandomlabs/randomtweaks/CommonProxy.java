package com.therandomlabs.randomtweaks;

import com.therandomlabs.randomtweaks.common.NetherPortalSpawnHandler;
import com.therandomlabs.randomtweaks.common.RTFoodStats;
import com.therandomlabs.randomtweaks.common.RTLanguageMap;
import com.therandomlabs.randomtweaks.common.SleepHandler;
import com.therandomlabs.randomtweaks.common.world.WorldGeneratorOceanFloor;
import com.therandomlabs.randomtweaks.common.world.WorldTypeRegistry;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
	public void preInit() {
		RTConfig.reload();
		RTLanguageMap.replaceLanguageMaps();

		if(RTConfig.misc.betterButtonNames) {
			Blocks.STONE_BUTTON.setTranslationKey("buttonStone");
			Blocks.WOODEN_BUTTON.setTranslationKey("buttonWood");
		}
	}

	public void init() {
		WorldTypeRegistry.registerWorldTypes();

		if(RTConfig.oceanFloor.enabled && !RandomTweaks.OCEAN_FLOOR_LOADED) {
			GameRegistry.registerWorldGenerator(new WorldGeneratorOceanFloor(), 0);
		}

		if(RTConfig.hunger.enabled && RandomTweaks.APPLECORE_LOADED) {
			MinecraftForge.EVENT_BUS.register(new RTFoodStats.AppleCoreEventHandler());
		}

		if(RandomTweaks.VERTICAL_END_PORTALS_LOADED) {
			MinecraftForge.EVENT_BUS.register(new NetherPortalSpawnHandler.VEPHandler());
		}

		if(RandomTweaks.COMFORTS_LOADED) {
			FMLInterModComms.sendFunctionMessage(
					"comforts", "mobSleepFilter", SleepHandler.MobFilter.class.getName()
			);
		}
	}
}
