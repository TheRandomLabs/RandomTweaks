package com.therandomlabs.randomtweaks.common.command;

import com.therandomlabs.randomlib.config.ConfigManager;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;

public class CommandRTReload extends CommandBase {
	private final boolean isClient;

	public CommandRTReload(Side side) {
		isClient = side.isClient();
	}

	@Override
	public String getName() {
		return isClient ? "rtreloadclient" : "rtreload";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return isClient ? "commands.rtreloadclient.usage" : "/rtreload";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
			throws CommandException {
		ConfigManager.reloadFromDisk(RTConfig.class);

		if(server != null && server.isDedicatedServer()) {
			notifyCommandListener(sender, this, "RandomTweaks configuration reloaded!");
		} else {
			sender.sendMessage(new TextComponentTranslation("commands.rtreloadclient.success"));
		}
	}

	@Override
	public int getRequiredPermissionLevel() {
		return isClient ? 0 : 4;
	}
}
