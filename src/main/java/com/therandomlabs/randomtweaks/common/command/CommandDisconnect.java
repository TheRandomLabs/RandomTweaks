package com.therandomlabs.randomtweaks.common.command;

import com.therandomlabs.randompatches.config.RPStaticConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.util.RTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.server.MinecraftServer;

public class CommandDisconnect extends CommandBase {
	private static final Minecraft mc = Minecraft.getMinecraft();

	@Override
	public String getName() {
		return "disconnect";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.disconnect.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
			throws CommandException {
		final boolean forceTitleScreenOnDisconnect;

		if(RandomTweaks.RANDOMPATCHES_LOADED) {
			try {
				forceTitleScreenOnDisconnect = RPStaticConfig.forceTitleScreenOnDisconnect;
			} catch(Throwable t) {
				RandomTweaks.LOGGER.error("Failed to disconnect", t);
				throw new CommandException(RTUtils.localize(
						"commands.disconnect.failure",
						t.getClass().getName() + ": " + t.getMessage()
				));
			}
		} else {
			forceTitleScreenOnDisconnect = false;
		}

		mc.world.sendQuittingDisconnectingPacket();
		mc.loadWorld(null);

		if(forceTitleScreenOnDisconnect || mc.isIntegratedServerRunning()) {
			mc.displayGuiScreen(new GuiMainMenu());
		} else if(mc.isConnectedToRealms()) {
			new RealmsBridge().switchToRealms(new GuiMainMenu());
		} else {
			mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
		}
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}
}
