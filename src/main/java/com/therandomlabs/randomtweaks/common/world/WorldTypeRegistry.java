package com.therandomlabs.randomtweaks.common.world;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.world.WorldType;

public final class WorldTypeRegistry {
	static final List<RTWorldType> worldTypes = new ArrayList<>();

	public static final WorldTypeRealistic REALISTIC;
	public static final WorldTypeVoid VOID;
	public static final WorldTypeVoidIslands VOID_ISLANDS;

	static {
		//Apparently some mods change the type of WorldType.WORLD_TYPES to force a world type
		//(namely Hex Lands)
		if(WorldType.WORLD_TYPES.getClass() != WorldType[].class) {
			REALISTIC = null;
			VOID = null;
			VOID_ISLANDS = null;
		} else {
			REALISTIC = new WorldTypeRealistic();
			VOID = new WorldTypeVoid();
			VOID_ISLANDS = new WorldTypeVoidIslands();
		}
	}

	public static void registerWorldTypes() {
		worldTypes.forEach(RTWorldType::onConfigReload);
	}

	public static boolean shouldRegisterRealisticWorldType() {
		if(!RTConfig.World.realisticWorldType) {
			return false;
		}

		if(!RandomTweaks.QUARK_LOADED) {
			return true;
		}

		try {
			final Class<?> moduleLoader = Class.forName("vazkii.quark.base.module.ModuleLoader");

			final Method isFeatureEnabled = moduleLoader.getDeclaredMethod(
					"isFeatureEnabled", Class.class
			);

			final Class<?> realisticWorldType =
					Class.forName("vazkii.quark.world.feature.RealisticWorldType");

			return !((boolean) isFeatureEnabled.invoke(null, realisticWorldType));
		} catch(Exception ex) {
			RandomTweaks.LOGGER.error(
					"Failed to check if Quark's realistic world type is enabled", ex
			);
		}

		return true;
	}
}
