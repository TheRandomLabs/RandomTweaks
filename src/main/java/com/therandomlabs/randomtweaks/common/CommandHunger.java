package com.therandomlabs.randomtweaks.common;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class CommandHunger extends CommandBase {
	@Override
	public String getName() {
		return "hunger";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return RandomTweaks.translate("commands.hunger.usage");
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
				HungerHandler.setSaturation(stats, (float) parseDouble(args[1]));
			} catch(Exception ex) {
				ex.printStackTrace();
				throw new CommandException(RandomTweaks.translate("commands.hunger.failure",
						ex.getClass().getName() + ": " + ex.getMessage()));
			}
		}

		notifyCommandListener(sender, this, RandomTweaks.translate("commands.hunger.success",
				player.getDisplayNameString(), stats.getFoodLevel(), stats.getSaturationLevel()));
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
			String[] args, BlockPos targetPos) {
		return args.length == 1 ?
				getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) :
					Collections.<String>emptyList();
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 0;
	}
}
