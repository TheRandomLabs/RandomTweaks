package com.therandomlabs.randomtweaks.common.worldtype;

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

		if(RTConfig.world.disableRealisticWorldTypeWithQuark &&
				(Loader.isModLoaded("Quark") || Loader.isModLoaded("quark"))) {
			return false;
		}

		return true;
	}
}
