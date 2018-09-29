package com.therandomlabs.randomtweaks;

import com.therandomlabs.randomtweaks.client.CreativeTabHandler;
import com.therandomlabs.randomtweaks.client.KeyBindingHandler;
import com.therandomlabs.randomtweaks.common.command.CommandRegistry;

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
}
