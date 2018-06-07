package com.therandomlabs.randomtweaks.common.command;

import com.therandomlabs.randomtweaks.base.RTConfig;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public final class CommandRegistry {
	public static void register(FMLServerStartingEvent event) {
		if(RTConfig.commands.deletegamerule) {
			event.registerServerCommand(new CommandDeleteGamerule());
		}

		if(RTConfig.commands.hunger) {
			event.registerServerCommand(new CommandHunger());
		}

		if(RTConfig.commands.giveTweaks) {
			event.registerServerCommand(new CommandRTGive());
		}

		if(RTConfig.commands.helpTweaks) {
			event.registerServerCommand(new CommandRTHelp());
		}

		if(RTConfig.commands.rtreload) {
			event.registerServerCommand(new CommandRTReload(false));
		}
	}

	public static void registerClient() {
		if(RTConfig.commands.disconnect) {
			ClientCommandHandler.instance.registerCommand(new CommandDisconnect());
		}

		if(RTConfig.commands.rtreloadclient) {
			ClientCommandHandler.instance.registerCommand(new CommandRTReload(true));
		}
	}

	public static void serverStarted(FMLServerStartedEvent event) {
		if(RTConfig.commands.helpTweaks) {
			CommandRTHelp.serverStarted();
		}
	}
}
