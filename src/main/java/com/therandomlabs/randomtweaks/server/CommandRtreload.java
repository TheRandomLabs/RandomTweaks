package com.therandomlabs.randomtweaks.server;

import com.therandomlabs.randomtweaks.common.RTConfig;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandRtreload extends CommandBase {
	@Override
	public String getName() {
		return "rtreload";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return Utils.localize("commands.rtreload.usage");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
			throws CommandException {
		try {
			RTConfig.reloadConfig();
			notifyCommandListener(sender, this, Utils.localize("commands.rtreload.success"));
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new CommandException(Utils.localize("commands.rtreload.failure",
					ex.getClass().getName() + ": " + ex.getMessage()));
		}
	}
}
