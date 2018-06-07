package com.therandomlabs.randomtweaks.common.command;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import com.therandomlabs.randomtweaks.base.RandomTweaks;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandHelp;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

//Taken and adapted from https://github.com/matthewprenger/HelpFixer, which is licensed
//under the WTFPL
public class CommandRTHelp extends CommandHelp {
	private static final ICommand testCommand1 = new TestCommand("a");
	private static final ICommand testCommand2 = new TestCommand("z");

	private static class TestCommand extends CommandBase {
		private final String name;

		TestCommand(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return null;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {}
	}

	@Override
	protected List<ICommand> getSortedPossibleCommands(ICommandSender sender,
			MinecraftServer server) {
		final List<ICommand> commands = server.getCommandManager().getPossibleCommands(sender);
		final Iterator<ICommand> it = commands.iterator();

		while(it.hasNext()) {
			final ICommand command = it.next();

			try {
				if(command.getName() == null) {
					RandomTweaks.LOGGER.warn("The command %s has a null name! Ignoring...",
							command.getClass().getName());
					it.remove();
				} else if(command.getUsage(sender) == null) {
					RandomTweaks.LOGGER.warn("The command %s has a null usage! Ignoring...",
							command.getClass().getName());
					it.remove();
				}
			} catch(Exception ex) {
				RandomTweaks.LOGGER.warn("Failed to test the command %s!", command, ex);
			}
		}

		commands.sort(Comparator.comparing(ICommand::getName));
		return commands;
	}

	static void serverStarted() {
		final Collection<ICommand> commands = FMLCommonHandler.instance().
				getMinecraftServerInstance().getCommandManager().getCommands().values();
		for(ICommand command : commands) {
			if(!isValid(command)) {
				RandomTweaks.LOGGER.warn("The command %s incorrectly overrides compareTo!",
						command.getName(), command.getClass().getName());
			}
		}
	}

	private static boolean isValid(ICommand command) {
		try {
			return command.compareTo(testCommand1) != command.compareTo(testCommand2);
		} catch(Exception ex) {
			RandomTweaks.LOGGER.warn("Failed to test the command %s for a valid compareTo!",
					command, ex);
		}

		//We don't know if the implementation is bad, just that it throws an exception
		return true;
	}
}
