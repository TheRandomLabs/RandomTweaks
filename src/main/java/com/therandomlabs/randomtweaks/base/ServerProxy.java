package com.therandomlabs.randomtweaks.base;

import com.therandomlabs.randomtweaks.server.TerminationHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends CommonProxy {
	static MinecraftServer server;
	static boolean enabled;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		enabled = true;
		super.preInit(event);

		if(RTConfig.server.catchTerminationSignals) {
			TerminationHandler.init();
		}
	}

	public static MinecraftServer getServer() {
		return server;
	}
}
