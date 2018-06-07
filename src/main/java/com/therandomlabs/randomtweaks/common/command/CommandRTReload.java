package com.therandomlabs.randomtweaks.common.command;

import com.therandomlabs.randomtweaks.base.RTConfig;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandRTReload extends CommandBase {
	private final boolean client;

	public CommandRTReload(boolean client) {
		this.client = client;
	}

	@Override
	public String getName() {
		return client ? "rtreloadclient" : "rtreload";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return client ? 0 : 4;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return Utils.localize("commands." + getName() + ".usage");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
			throws CommandException {
		try {
			RTConfig.reloadConfig();
			if(client) {
				sender.sendMessage(
						new TextComponentString(Utils.localize("commands.rtreload.success")));
			} else {
				notifyCommandListener(sender, this, Utils.localize("commands.rtreload.success"));
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new CommandException(Utils.localize("commands.rtreload.failure",
					ex.getClass().getName() + ": " + ex.getMessage()));
		}
	}
}
