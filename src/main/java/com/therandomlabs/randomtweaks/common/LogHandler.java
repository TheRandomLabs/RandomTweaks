package com.therandomlabs.randomtweaks.common;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import net.minecraft.client.Minecraft;

public final class LogHandler extends PrintStream {
	public static final PrintStream originalOut = System.out;
	public static final PrintStream out = new LogHandler();

	public LogHandler() {
		super(originalOut);
	}

	@Override
	public void print(String string) {
		if(string.contains("INFO") || string.contains("WARN")) {
			return;
		}

		super.print(string);
	}

	public static void updateLogFilters() {
		System.out.println(RandomTweaks.LOGGER.getClass() + " HELLO");
		System.setOut(out); //TODO
	}
}
