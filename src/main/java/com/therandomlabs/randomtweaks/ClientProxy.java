package com.therandomlabs.randomtweaks;

import com.therandomlabs.randomtweaks.client.CreativeTabHandler;
import com.therandomlabs.randomtweaks.client.NightVisionHandler;
import com.therandomlabs.randomtweaks.common.command.CommandRegistry;
import com.therandomlabs.randomtweaks.config.RTConfig;

public final class ClientProxy extends CommonProxy {
	@Override
	public void preInit() {
		super.preInit();
		CreativeTabHandler.init();
		CommandRegistry.registerClient();
	}

	@Override
	public void postInit() {
		if(RTConfig.NightVision.enabled) {
			NightVisionHandler.initialize();
		}
	}
}
