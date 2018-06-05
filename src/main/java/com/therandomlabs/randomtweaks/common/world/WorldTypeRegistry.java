package com.therandomlabs.randomtweaks.common.world;

import java.lang.reflect.Method;
import com.therandomlabs.randomtweaks.common.RTConfig;
import net.minecraftforge.fml.common.Loader;

public final class WorldTypeRegistry {
	public static void registerWorldTypes() {
		if(shouldRegisterRealisticWorldType()) {
			new WorldTypeRealistic();
		}

		if(RTConfig.world.voidWorldType) {
			new WorldTypeVoid();
		}

		if(RTConfig.world.voidIslandsWorldType) {
			new WorldTypeVoidIslands();
		}
	}

	public static boolean shouldRegisterRealisticWorldType() {
		if(!RTConfig.world.realisticWorldType) {
			return false;
		}

		if(Loader.isModLoaded("quark")) {
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
}
