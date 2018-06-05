package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.command.CommandRegistry;
import com.therandomlabs.randomtweaks.common.CommonProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public final class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent event) throws Exception {
		super.preInit(event);
		CreativeTabHandler.init();
		CommandRegistry.registerClient();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		KeyBindingHandler.registerKeyBindings();
	}
}
