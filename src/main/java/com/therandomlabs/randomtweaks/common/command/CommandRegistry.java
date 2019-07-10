package com.therandomlabs.randomtweaks.common.command;

import com.therandomlabs.randomlib.config.CommandConfigReload;
import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public final class CommandRegistry {
	public static void register(FMLServerStartingEvent event) {
		if(RTConfig.Commands.deletegamerule) {
			event.registerServerCommand(new CommandDeleteGamerule());
		}

		if(RTConfig.Commands.hunger) {
			event.registerServerCommand(new CommandHunger());
		}

		if(RTConfig.Commands.giveTweaks) {
			event.registerServerCommand(new CommandRTGive());
		}

		if(RTConfig.Commands.helpTweaks && !RandomTweaks.HELPFIXER_LOADED) {
			event.registerServerCommand(new CommandRTHelp());
		}

		if(RTConfig.Commands.rtreload) {
			event.registerServerCommand(CommandConfigReload.server(
					"rtreload", "rtreloadclient", RTConfig.class,
					"RandomTweaks configuration reloaded!"
			));
		}
	}

	public static void registerClient() {
		if(RTConfig.Commands.rtreloadclient) {
			ClientCommandHandler.instance.registerCommand(CommandConfigReload.client(
					"rtreloadclient", RTConfig.class
			));
		}

		if(RTConfig.Commands.disconnect) {
			ClientCommandHandler.instance.registerCommand(new CommandDisconnect());
		}
	}

	public static void serverStarted() {
		if(RTConfig.Commands.helpTweaks) {
			CommandRTHelp.serverStarted();
		}
	}
}
