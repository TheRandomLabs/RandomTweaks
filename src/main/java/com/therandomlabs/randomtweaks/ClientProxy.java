package com.therandomlabs.randomtweaks;

import com.therandomlabs.randomtweaks.client.CreativeTabHandler;
import com.therandomlabs.randomtweaks.client.KeyBindingHandler;
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
	public void init() {
		super.init();
		KeyBindingHandler.registerKeyBindings();
	}

	@Override
	public void postInit() {
		if(RTConfig.NightVision.enabled && !RandomTweaks.NO_NIGHT_VISION_FLASHING_LOADED) {
			NightVisionHandler.initialize();
		}
	}
}
