package com.therandomlabs.randomtweaks.command;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class CommandDeleteGamerule extends CommandBase {
	public static final Field GAMERULES =
			ReflectionHelper.findField(GameRules.class, "rules", "field_82771_a");

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

		try {
			((Map<String, ?>) GAMERULES.get(sender.getEntityWorld().getGameRules())).
					remove(args[0]);
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new CommandException(Utils.localize("commands.deletegamerule.failure",
					ex.getClass().getName() + ": " + ex.getMessage()));
		}

		notifyCommandListener(sender, this,
				Utils.localize("commands.deletegamerule.success", args[0]));
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
			String[] args, BlockPos targetPos) {
		return Arrays.asList(sender.getEntityWorld().getGameRules().getRules());
	}
}
