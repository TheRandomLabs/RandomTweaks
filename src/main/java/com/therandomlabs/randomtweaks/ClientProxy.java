package com.therandomlabs.randomtweaks;

import com.therandomlabs.randomtweaks.client.CapeHandler;
import com.therandomlabs.randomtweaks.client.NightVisionHandler;
import com.therandomlabs.randomtweaks.common.command.CommandRegistry;
import com.therandomlabs.randomtweaks.config.RTConfig;

public final class ClientProxy extends CommonProxy {
	@Override
	public void preInit() {
		super.preInit();

		if(RTConfig.Client.contributorCapes) {
			CapeHandler.downloadPlayers();
		}

		CommandRegistry.registerClient();
	}

	@Override
	public void postInit() {
		if(RTConfig.NightVision.enabled && !RandomTweaks.NO_NIGHT_VISION_FLASHING_LOADED &&
				!RandomTweaks.POTION_CORE_LOADED) {
			NightVisionHandler.initialize();
		}
	}
}
