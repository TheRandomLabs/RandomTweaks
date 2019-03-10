package com.therandomlabs.randomtweaks.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.therandomlabs.randomtweaks.RandomTweaks;
import org.apache.commons.lang3.StringUtils;

public final class RTData {
	private static RTData data;

	public Map<String, Boolean> timeOfDayOverlay;
	public boolean stepup;
	public boolean fovChanges;

	public static RTData get() {
		if(data != null) {
			return data;
		}

		final Path path = getPath();

		if(path.toFile().exists()) {
			try {
				data = new Gson().fromJson(read(path), RTData.class);
			} catch(JsonSyntaxException ex) {
				RandomTweaks.LOGGER.error(
						"Error in the RandomTweaks data JSON. The file will be replaced.", ex
				);
			}
		}

		if(data == null) {
			data = new RTData();

			data.timeOfDayOverlay = new HashMap<>();
			data.stepup = RTConfig.Client.stepupEnabledByDefault;
			data.fovChanges = RTConfig.Keybinds.fovChangesEnabledByDefault;

			save();
		} else if(data.timeOfDayOverlay == null) {
			data.timeOfDayOverlay = new HashMap<>();
			save();
		}

		return data;
	}

	public static void save() {
		//Ensure non-null
		get();

		try {
			Files.write(getPath(), Collections.singletonList(new Gson().toJson(data)));
		} catch(IOException ex) {
			RandomTweaks.LOGGER.error("Error while saving RandomTweaks data", ex);
		}
	}

	public static Path getPath() {
		final Path path;

		if(RTConfig.Client.storeDataInLocal) {
			path = Paths.get("local", "client", "rtdata.json");
		} else {
			path = Paths.get("config", RandomTweaks.MOD_ID, "data.json");
		}

		final Path parent = path.getParent();

		try {
			if(parent != null) {
				if(Files.isRegularFile(parent)) {
					Files.delete(parent);
				}

				Files.createDirectories(parent);
			}
		} catch(IOException ex) {
			RandomTweaks.LOGGER.error("Failed to create parent: " + path, ex);
		}

		return path;
	}

	private static String read(Path path) {
		try {
			return StringUtils.join(Files.readAllLines(path), System.lineSeparator());
		} catch(IOException ex) {
			RandomTweaks.LOGGER.error("Failed to read file: " + path, ex);
		}

		return "{}";
	}
}
