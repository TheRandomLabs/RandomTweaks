package com.therandomlabs.randomtweaks.server;

import com.therandomlabs.randomtweaks.common.ConfigurationHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public final class CommandRegistry {
	public static void register(FMLServerStartingEvent event) {
		if(ConfigurationHandler.deletegameruleCommand) {
			event.registerServerCommand(new CommandDeleteGamerule());
		}

		if(ConfigurationHandler.hungerCommand) {
			event.registerServerCommand(new CommandHunger());
		}

		if(ConfigurationHandler.giveCommandTweaks) {
			event.registerServerCommand(new CommandGive());
		}

		if(ConfigurationHandler.rtreloadCommand) {
			event.registerServerCommand(new CommandRtreload());
		}
	}
}
