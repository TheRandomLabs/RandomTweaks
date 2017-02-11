package com.therandomlabs.randomtweaks.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.MalformedJsonException;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

//TODO rewrite this mess
public final class ConfigurationHandler {
	public static final String RANDOMTWEAKS = RandomTweaks.MODID + ".json";
	public static final String DEFAULT_GAMERULES = "defaultgamerules.json";
	private static Path directory;

	static void initialize(FMLPreInitializationEvent event) throws IOException {
		directory =
				Paths.get(event.getSuggestedConfigurationFile().getParentFile().getAbsolutePath(),
				RandomTweaks.MODID);
		if(!Files.exists(directory)) {
			Files.createDirectory(directory);
		}

		if(!configurationExists(RANDOMTWEAKS)) {
			createConfiguration();
		}
		if(!configurationExists(DEFAULT_GAMERULES)) {
			createDefaultGamerulesConfiguration();
		}
	}

	public static void createConfiguration() throws IOException {
		Files.write(getConfiguration(RANDOMTWEAKS), Arrays.asList(
				"{",
				"\t\"reloadSoundSystem\": true,",
				"\t\"moreRomanNumerals\": true,",
				"\t\"deletegameruleCommand\": true,",
				"\t\"hungerCommand\": true,",
				"\t\"dontResetHungerOnRespawn\": false,",
				"\t\"minimumHungerLevelOnRespawn\": 3",
				"}"
		));
	}

	public static void createDefaultGamerulesConfiguration() throws IOException {
		Files.write(getConfiguration(DEFAULT_GAMERULES), Arrays.asList(
				"//Example configuration - game does not need to be restarted when changing this",
				"{",
				"//\t\"commandBlockOutput\": false, //These are for all game modes",
				"//\t\"keepInventory\": true,",
				"//\t\"1\": { //This is for a specific game mode (creative)",
				"//\t\t\"doDaylightCycle\": false,",
				"//\t\t\"doWeatherCycle\": false,",
				"//\t\t\"doMobSpawning\": false",
				"//\t}",
				"}"
		));
	}

	public static boolean readBoolean(String key) throws IOException {
		if(!configurationExists(RANDOMTWEAKS)) {
			createConfiguration();
		}

		try {
			return readJson(RANDOMTWEAKS).get(key).getAsBoolean();
		} catch(Exception ex) {
			createConfiguration();
			return readJson(RANDOMTWEAKS).get(key).getAsBoolean();
		}
	}

	public static int readInteger(String key) throws IOException {
		if(!configurationExists(RANDOMTWEAKS)) {
			createConfiguration();
		}

		try {
			return readJson(RANDOMTWEAKS).get(key).getAsInt();
		} catch(Exception ex) {
			createConfiguration();
			return readJson(RANDOMTWEAKS).get(key).getAsInt();
		}
	}

	public static Map<String, String> getDefaultGamerules(int gamemode) throws IOException {
		if(!configurationExists(DEFAULT_GAMERULES)) {
			createDefaultGamerulesConfiguration();
		}

		JsonObject object = null;
		try {
			object = readJson(DEFAULT_GAMERULES);
		} catch(MalformedJsonException ex) {
			return null;
		}

		final Map<String, String> gamerules = new HashMap<>();

		for(Entry<String, JsonElement> entry : object.entrySet()) {
			if(entry.getValue().isJsonObject()) {
				try {
					if(Integer.parseInt(entry.getKey()) == gamemode) {
						getDefaultGamerules(entry.getValue().getAsJsonObject(), gamerules);
					}
				} catch(NumberFormatException ex) {}
				continue;
			}

			gamerules.put(entry.getKey(), entry.getValue().toString());
		}

		return gamerules;
	}

	private static void getDefaultGamerules(JsonObject object, Map<String, String> gamerules) {
		for(Entry<String, JsonElement> entry : object.entrySet()) {
			gamerules.put(entry.getKey(), entry.getValue().toString());
		}
	}

	public static boolean isString(JsonElement element) {
		return element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
	}

	public static Path getConfiguration(String name) {
		return Paths.get(directory.toString(), name);
	}

	public static JsonObject readJson(String name) throws IOException {
		return new JsonParser().parse(readConfiguration(name)).getAsJsonObject();
	}

	public static String readConfiguration(String name) throws IOException {
		return StringUtils.join(Files.readAllLines(getConfiguration(name)),
				System.lineSeparator());
	}

	public static boolean configurationExists(String name) {
		return Files.exists(getConfiguration(name));
	}
}
