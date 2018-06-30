package com.therandomlabs.randomtweaks.server;

import java.util.Set;
import com.google.common.collect.Sets;
import com.therandomlabs.randomtweaks.base.RandomTweaks;
import com.therandomlabs.randomtweaks.base.ServerProxy;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sun.misc.Signal;
import sun.misc.SignalHandler;

//Taken and adapted from:
//https://github.com/Baughn/signal-catcher/blob/master/src/main/java/info/brage/minecraft/
//signalcatcher/SignalCatcher.java
public final class TerminationHandler {
	public static final Set<String> SIGNALS = Sets.newHashSet(
			"TERM",
			"INT",
			"HUP"
	);

	public static final SignalHandler SIGNAL_HANDLER = new SignalHandler() {
		@Override
		public void handle(Signal signal) {
			if(signalCount == 0) {
				RandomTweaks.LOGGER.info("Terminating...");

				if(server == null) {
					RandomTweaks.LOGGER.warn("No MinecraftServer found. " +
							"Ignoring termination signal...");
				} else {
					server.initiateShutdown();
				}

				RandomTweaks.LOGGER.info("A second signal will trigger a standard Forge " +
						"shutdown, and a third will trigger abrupt termination, which " +
						"may cause world corruption.");
			} else if(signalCount == 1) {
				RandomTweaks.LOGGER.info("Triggering standard Forge shutdown...");
				FMLCommonHandler.instance().exitJava(1, false);
			} else {
				RandomTweaks.LOGGER.info("Triggering abrupt termination...");
				FMLCommonHandler.instance().exitJava(2, true);
			}

			signalCount++;
		}
	};

	private static final MinecraftServer server = ServerProxy.getServer();
	private static int signalCount;

	private TerminationHandler() {}

	public static void init() {
		SIGNALS.forEach(signal -> Signal.handle(new Signal(signal), SIGNAL_HANDLER));
	}
}
