package com.therandomlabs.randomtweaks.common.worldtype;

import com.therandomlabs.randomtweaks.common.ConfigurationHandler;

public final class WorldTypeRegistry {
	public static void registerWorldTypes() {
		if(ConfigurationHandler.realisticWorldType) {
			WorldTypeRealistic.INSTANCE.getClass();
		}

		if(ConfigurationHandler.voidWorldType) {
			WorldTypeVoid.INSTANCE.getClass();
		}

		if(ConfigurationHandler.voidIslandsWorldType) {
			WorldTypeVoidIslands.INSTANCE.getClass();
		}
	}
}
