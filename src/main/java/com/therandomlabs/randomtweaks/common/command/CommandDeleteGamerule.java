package com.therandomlabs.randomtweaks.common.command;

import java.util.Arrays;
import java.util.List;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandDeleteGamerule extends CommandBase {
	@Override
	public String getName() {
		return "deletegamerule";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return Utils.localize("commands.deletegamerule.usage");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
			throws CommandException {
		if(args.length == 0) {
			throw new WrongUsageException(getUsage(sender));
		}

		final boolean failed = sender.getEntityWorld().getGameRules().rules.remove(args[0]) == null;
		final String message = failed ? "noSuchGameruleExists" : "success";
		notifyCommandListener(sender, this, Utils.localize("commands.deletegamerule." + message),
				args[0]);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
			String[] args, BlockPos targetPos) {
		return Arrays.asList(sender.getEntityWorld().getGameRules().getRules());
	}
}
