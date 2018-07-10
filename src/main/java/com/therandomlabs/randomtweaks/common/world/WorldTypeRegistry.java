package com.therandomlabs.randomtweaks.common.world;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RandomTweaks.MODID)
public final class WorldTypeRegistry {
	static final List<RTWorldType> WORLD_TYPES = new ArrayList<>();

	public static final WorldTypeRealistic REALISTIC = new WorldTypeRealistic();
	public static final WorldTypeVoid VOID = new WorldTypeVoid();
	public static final WorldTypeVoidIslands VOID_IOSLANDS = new WorldTypeVoidIslands();

	public static void registerWorldTypes() {
		WORLD_TYPES.forEach(RTWorldType::onConfigReload);
	}

	public static boolean shouldRegisterRealisticWorldType() {
		if(!RTConfig.world.realisticWorldType) {
			return false;
		}

		if(RandomTweaks.QUARK_LOADED) {
			try {
				final Class<?> moduleLoader =
						Class.forName("vazkii.quark.base.module.ModuleLoader");
				final Method isFeatureEnabled =
						moduleLoader.getDeclaredMethod("isFeatureEnabled", Class.class);
				final Class<?> realisticWorldType =
						Class.forName("vazkii.quark.world.feature.RealisticWorldType");
				return !((boolean) isFeatureEnabled.invoke(null, realisticWorldType));
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}

		return true;
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.PostConfigChangedEvent event) {
		if(event.getModID().equals(RandomTweaks.MODID)) {
			registerWorldTypes();
		}
	}
}
