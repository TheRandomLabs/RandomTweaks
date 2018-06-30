package com.therandomlabs.randomtweaks.base;

import java.util.Set;
import com.google.common.collect.Sets;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import sun.misc.Signal;
import sun.misc.SignalHandler;

//Taken and adapted from:
//https://github.com/Baughn/signal-catcher/blob/master/src/main/java/info/brage/minecraft/
//signalcatcher/SignalCatcher.java
public class ServerProxy extends CommonProxy {
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

	private static int signalCount;
	static MinecraftServer server;
	static boolean enabled;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		enabled = true;
		super.preInit(event);

		if(RTConfig.server.catchTerminationSignals) {
			SIGNALS.forEach(signal -> Signal.handle(new Signal(signal), SIGNAL_HANDLER));
		}
	}
}
