package com.therandomlabs.randomtweaks.common.command;

import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;

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

		if(RTConfig.commands.helpTweaks && !RandomTweaks.HELPFIXER_LOADED) {
			event.registerServerCommand(new CommandRTHelp());
		}

		if(RTConfig.commands.rtreload) {
			event.registerServerCommand(new CommandRTReload(Side.SERVER));
		}
	}

	public static void registerClient() {
		if(RTConfig.commands.rtreloadclient) {
			ClientCommandHandler.instance.registerCommand(new CommandRTReload(Side.CLIENT));
		}

		if(RTConfig.commands.disconnect) {
			ClientCommandHandler.instance.registerCommand(new CommandDisconnect());
		}
	}

	public static void serverStarted() {
		if(RTConfig.commands.helpTweaks) {
			CommandRTHelp.serverStarted();
		}
	}
}
