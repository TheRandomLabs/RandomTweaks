package com.therandomlabs.randomtweaks.base;

import com.therandomlabs.randomtweaks.client.CreativeTabHandler;
import com.therandomlabs.randomtweaks.client.KeyBindingHandler;
import com.therandomlabs.randomtweaks.common.command.CommandRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public final class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent event) {
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
