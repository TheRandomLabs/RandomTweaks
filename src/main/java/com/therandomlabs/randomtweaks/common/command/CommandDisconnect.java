package com.therandomlabs.randomtweaks.common.command;

import java.lang.reflect.Field;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Loader;

public class CommandDisconnect extends CommandBase {
	private static final Minecraft mc = Minecraft.getMinecraft();

	@Override
	public String getName() {
		return "disconnect";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.disconnect.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
			throws CommandException {
		try {
			boolean forceTitleScreenOnDisconnect = false;

			if(Loader.isModLoaded("randompatches")) {
				final Class<?> rpConfig = Class.forName("com.therandomlabs.randompatches.RPConfig");
				final Field field = rpConfig.getDeclaredField("forceTitleScreenOnDisconnect");
				forceTitleScreenOnDisconnect = (boolean) field.get(null);
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
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new CommandException(Utils.localize("commands.disconnect.failure",
					ex.getClass().getName() + ": " + ex.getMessage()));
		}
	}
}
