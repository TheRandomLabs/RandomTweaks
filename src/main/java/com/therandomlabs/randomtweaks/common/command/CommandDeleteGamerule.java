package com.therandomlabs.randomtweaks.common.command;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class CommandDeleteGamerule extends CommandBase {
	@Override
	public String getName() {
		return "deletegamerule";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/deletegamerule <gamerule>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
			throws CommandException {
		if(args.length == 0) {
			throw new WrongUsageException(getUsage(sender));
		}

		final boolean success =
				sender.getEntityWorld().getGameRules().rules.remove(args[0]) != null;

		if(success) {
			notifyCommandListener(sender, this, "Gamerule \"%s\" successfully deleted!", args[0]);
		} else {
			notifyCommandListener(
					sender, this, TextFormatting.RED + "No such gamerule exists: %s" + args[0]
			);
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
			String[] args, BlockPos targetPos) {
		return Arrays.asList(sender.getEntityWorld().getGameRules().getRules());
	}
}
