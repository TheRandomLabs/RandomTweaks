package com.therandomlabs.randomtweaks.server;

import java.util.Collections;
import java.util.List;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.BlockPos;

public class CommandHunger extends CommandBase {
	@Override
	public String getName() {
		return "hunger";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return Utils.localize("commands.hunger.usage");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
			throws CommandException {
		if(args.length < 2) {
			throw new WrongUsageException(getUsage(sender));
		}

		final EntityPlayer player = getPlayer(server, sender, args[0]);
		final FoodStats stats = player.getFoodStats();

		stats.setFoodLevel(parseInt(args[1]));

		if(args.length > 2) {
			try {
				Utils.setSaturation(stats, (float) parseDouble(args[2]));
			} catch(Exception ex) {
				ex.printStackTrace();
				throw new CommandException(Utils.localize("commands.hunger.failure",
						ex.getClass().getName() + ": " + ex.getMessage()));
			}
		}

		notifyCommandListener(sender, this, Utils.localize("commands.hunger.success",
				player.getDisplayNameString(), stats.getFoodLevel(), stats.getSaturationLevel()));
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
			String[] args, BlockPos targetPos) {
		return args.length == 1 ?
				getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) :
				Collections.emptyList();
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 0;
	}
}
