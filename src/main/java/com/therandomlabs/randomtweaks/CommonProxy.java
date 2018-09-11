package com.therandomlabs.randomtweaks;

import com.therandomlabs.randomtweaks.common.RTFoodStats;
import com.therandomlabs.randomtweaks.common.RTLanguageMap;
import com.therandomlabs.randomtweaks.common.SleepHandler;
import com.therandomlabs.randomtweaks.common.TrampleHandler;
import com.therandomlabs.randomtweaks.common.world.WorldGeneratorOceanFloor;
import com.therandomlabs.randomtweaks.common.world.WorldTypeRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {
		RTConfig.reload();
		RTLanguageMap.replaceLanguageMaps();
	}

	public void init(FMLInitializationEvent event) {
		try {
			Class.forName("net.minecraftforge.event.world.BlockEvent$FarmlandTrampleEvent");
			MinecraftForge.EVENT_BUS.register(new TrampleHandler());
		} catch(ClassNotFoundException ignored) {}

		WorldTypeRegistry.registerWorldTypes();

		if(RTConfig.oceanFloor.enabled && !RandomTweaks.OCEAN_FLOOR_LOADED) {
			GameRegistry.registerWorldGenerator(new WorldGeneratorOceanFloor(), 0);
		}

		if(RTConfig.hunger.enabled && RandomTweaks.APPLECORE_LOADED) {
			MinecraftForge.EVENT_BUS.register(new RTFoodStats.AppleCoreEventHandler());
		}

		if(RandomTweaks.COMFORTS_LOADED) {
			FMLInterModComms.sendFunctionMessage("comforts", "mobSleepFilter",
					SleepHandler.MobFilter.class.getName());
		}
	}
}
