package com.therandomlabs.randomtweaks.server;

import com.therandomlabs.randomtweaks.common.RTConfig;
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
			event.registerServerCommand(new CommandGive());
		}

		if(RTConfig.commands.rtreload) {
			event.registerServerCommand(new CommandRtreload());
		}
	}
}
