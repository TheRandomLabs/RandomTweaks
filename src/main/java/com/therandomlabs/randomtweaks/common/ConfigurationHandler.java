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
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public final class ConfigurationHandler {
	public static final String RANDOMTWEAKS = RandomTweaks.MODID + ".cfg";
	public static final String DEFAULT_GAMERULES = "defaultgamerules.json";
	private static Path directory;
	private static Configuration configuration;

	public static boolean reloadSoundSystemKeyBind;
	public static boolean moreRomanNumerals;
	public static boolean moveBucketCreativeTab;
	public static boolean ocelotsCanBeHealed;
	public static boolean sleepTweaks;

	public static boolean realisticWorldType;
	public static boolean voidWorldType;
	public static int voidWorldTypeYSpawn;
	public static String voidWorldTypeBlock;
	public static int voidWorldTypeMeta;
	public static String voidWorldTypeBiome;

	public static final int SQUID_SPAWNING_DISABLED = -1;
	public static final int SQUID_CHUNK_LIMIT_DISABLED = 0;
	public static final int SQUID_SPAWN_LIMIT_RADIUS_DISABLED = 0;
	public static final int DEFAULT_SQUID_PACK_SIZE = 0;

	public static int squidSpawnLimitRadius;
	public static int squidChunkLimit;
	public static int maxSquidPackSize;

	public static boolean deletegameruleCommand;
	public static boolean hungerCommand;
	public static boolean giveCommandAcceptsIntegerIDs;
	public static boolean rtreloadCommand;

	public static final int RESET_HUNGER_ON_RESPAWN = 0;
	public static final int DONT_RESET_HUNGER_ON_RESPAWN = 1;
	public static final int DONT_RESET_HUNGER_IF_KEEPINVENTORY = 2;
	public static final int DONT_RESET_HUNGER_IF_KEEPINVENTORY_AND_NOT_CREATIVE = 3;

	public static int hungerRespawnBehavior;
	public static int minimumHungerLevelOnRespawn;

	private static boolean loaded;

	static void initialize(FMLPreInitializationEvent event) throws IOException {
		directory =
				Paths.get(event.getSuggestedConfigurationFile().getParentFile().getAbsolutePath(),
				RandomTweaks.MODID);
		if(!Files.exists(directory)) {
			Files.createDirectory(directory);
		}

		configuration = new Configuration(getConfiguration(RANDOMTWEAKS).toFile());

		reloadConfiguration();
		if(!configurationExists(DEFAULT_GAMERULES)) {
			createDefaultGamerulesConfiguration();
		}
	}

	public static void reloadConfiguration() throws IOException {
		configuration.load();

		if(!loaded) {
			reloadSoundSystemKeyBind = configuration.get("general", "reloadSoundSystemKeyBind",
					true, "Self explanatory. Client-sided.").getBoolean();
		}
		moreRomanNumerals = configuration.get("general", "moreRomanNumerals", true,
				"Self explanatory. Client-sided but also works on servers.").getBoolean();
		moveBucketCreativeTab = configuration.get("general", "moveBucketCreativeTab", true,
				"Moves the bucket to the Tools creative tab. Client-sided.").getBoolean();
		ocelotsCanBeHealed = configuration.get("general", "ocelotsCanBeHealed", true,
				"Ocelots can be healed with fish. Server-sided.").getBoolean();
		sleepTweaks = configuration.get("general", "sleepTweaks", true, "Players can sleep " +
				"around non-aggressive zombie pigmen and mobs with custom names. On 1.10, " +
				"adds a \"bed is too far away\" message. Server-sided.").getBoolean();

		realisticWorldType = configuration.get("world", "realisticWorldType", true,
				"Adds the Realistic world type. Name: realistic").getBoolean();
		voidWorldType = configuration.get("world", "voidWorldType", true,
				"Adds the Void world type. Name: void").getBoolean();
		voidWorldTypeYSpawn = configuration.get("world", "voidWorldTypeYSpawn", 17,
				"The Y coordinate of the default spawn point in a Void world.", 1, 255).getInt();
		voidWorldTypeBlock = configuration.get("world", "voidWorldTypeBlock", "minecraft:glass",
				"What block should be placed in the void world type for players to stand on. " +
				"Glass by default.").getString();
		voidWorldTypeMeta = configuration.get("world", "voidWorldTypeYMeta", 0,
				"The damage value for voidWorldTypeBlock.").getInt();
		voidWorldTypeBiome = configuration.get("world", "voidWorldTypeBiome", "minecraft:plains",
				"What biome the Void world type to use. Set to an empty string to use " +
				"Minecraft's default behavior.").getString();

		squidSpawnLimitRadius =
				configuration.get("squids", "squidSpawnLimitRadius", 64, "Disables squid " +
				"spawning if a player is not within the specified radius. Set to 0 to " +
				"disable this limit. Server-sided.", 0, Integer.MAX_VALUE).getInt();
		squidChunkLimit =
				configuration.get("squids", "squidChunkLimit", 10,
				"Limits the amount of squids allowed in a chunk. Set to 0 to disable squid " +
				"spawning, and set to -1 to disable this limit. Server-sided.",
				SQUID_CHUNK_LIMIT_DISABLED, Integer.MAX_VALUE).getInt();
		maxSquidPackSize = configuration.get("squids", "maxSquidPackSize", 2, "The maximum " +
				"amount of squids that can be spawned in a \"pack\". Set to 0 to use the " +
				"vanilla default. Server-sided.", DEFAULT_SQUID_PACK_SIZE,
				Integer.MAX_VALUE).getInt();

		deletegameruleCommand = configuration.get("commands", "deletegameruleCommand", true,
				"Self explanatory.").getBoolean();
		hungerCommand = configuration.get("commands", "hungerCommand", true,
				"Self explanatory.").getBoolean();
		giveCommandAcceptsIntegerIDs = configuration.get("commands",
				"giveCommandAcceptsIntegerIDs", true, "Self explanatory.").getBoolean();
		rtreloadCommand = configuration.get("general", "rtreloadCommand", true,
				"Reloads this configuration. Almost every value will be reloaded.").getBoolean();

		hungerRespawnBehavior = configuration.get("balance", "hungerRespawnBehavior",
				DONT_RESET_HUNGER_IF_KEEPINVENTORY_AND_NOT_CREATIVE, "0 = hunger resets on " +
				"respawn; 1 = hunger doesn't reset on respawn; 2 = hunger doesn't reset on " +
				"respawn if keepInventory is true; 3 = 2, but only if the player is not in " +
				"creative", RESET_HUNGER_ON_RESPAWN,
				DONT_RESET_HUNGER_IF_KEEPINVENTORY_AND_NOT_CREATIVE).getInt();
		minimumHungerLevelOnRespawn = configuration.get("balance",
				"minimumHungerLevelOnRespawn", 3, "If dontResetHungerOnRespawn is enabled, " +
				"this sets the minimum hunger on respawn so a player doesn't spawn with 0 " +
				"hunger.", 0, Integer.MAX_VALUE).getInt();

		configuration.save();

		loaded = true;
	}

	public static void createDefaultGamerulesConfiguration() throws IOException {
		Files.write(getConfiguration(DEFAULT_GAMERULES), Arrays.asList(
				"//Example configuration - game does not need to be restarted when changing this",
				"{",
				"//\t\"commandBlockOutput\": false, //These are for all game modes",
				"//\t\"keepInventory\": true,",
				"//\t\"1:flat,void\": { //Creative flat world. Game modes and world types can " +
						"be separated with commas (no spaces). These are the same as the world " +
						"types in server.properties.",
				"//\t\t\"doDaylightCycle\": false,",
				"//\t\t\"doWeatherCycle\": false,",
				"//\t\t\"doMobSpawning\": false",
				"//\t}",
				"}"
		));
	}

	public static Map<String, String> getDefaultGamerules(int gamemode, String worldType)
			throws IOException {
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
					final String[] split = entry.getKey().split(":");

					final String[] gamemodes = split[0].split(",");
					boolean gamemodeFound = false;

					for(String mode : gamemodes) {
						try {
							if(Integer.parseInt(mode) == gamemode) {
								gamemodeFound = true;
								break;
							}
						} catch(NumberFormatException ex) {}
					}

					if(!gamemodeFound) {
						continue;
					}

					if(split.length > 1) {
						final String[] worldTypes = split[1].split(",");
						boolean worldTypeFound = false;

						for(String type : worldTypes) {
							if(type.equals(worldType)) {
								worldTypeFound = true;
								break;
							}
						}

						if(!worldTypeFound) {
							continue;
						}
					}

					getDefaultGamerules(entry.getValue().getAsJsonObject(), gamerules);
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
