package com.therandomlabs.randomtweaks.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.therandomlabs.randomtweaks.RandomTweaks;

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
				data = new Gson().fromJson(RTConfig.readFile(path), RTData.class);
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
		return RTConfig.Client.storeDataInLocal ?
				RTConfig.getJson("../../local/client/rtdata") : RTConfig.getJson("data");
	}
}
