package com.therandomlabs.randomtweaks.client.command;

import com.therandomlabs.randomtweaks.common.RTConfig;
import net.minecraftforge.client.ClientCommandHandler;

public final class ClientCommandRegistry {
	public static void register() {
		if(RTConfig.commands.rtreloadclient) {
			ClientCommandHandler.instance.registerCommand(new CommandRtreloadClient());
		}
	}
}
