package com.therandomlabs.randomtweaks.common.worldtype;

import com.therandomlabs.randomtweaks.common.ConfigurationHandler;
import net.minecraftforge.fml.common.Loader;

public final class WorldTypeRegistry {
	public static void registerWorldTypes() {
		if(shouldRegisterRealisticWorldType()) {
			new WorldTypeRealistic();
		}

		if(ConfigurationHandler.voidWorldType) {
			new WorldTypeVoid();
		}

		if(ConfigurationHandler.voidIslandsWorldType) {
			new WorldTypeVoidIslands();
		}
	}

	public static boolean shouldRegisterRealisticWorldType() {
		if(!ConfigurationHandler.realisticWorldType) {
			return false;
		}

		if(ConfigurationHandler.disableRealisticWorldTypeWithQuark &&
				(Loader.isModLoaded("Quark") || Loader.isModLoaded("quark"))) {
			return false;
		}

		return true;
	}
}
