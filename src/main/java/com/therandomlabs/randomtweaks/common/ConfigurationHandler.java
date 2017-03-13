package com.therandomlabs.randomtweaks.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.MalformedJsonException;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public final class ConfigurationHandler {
	public static final String RANDOMTWEAKS = RandomTweaks.MODID + ".cfg";
	public static final String DEFAULT_GAMERULES = "defaultgamerules.json";
	private static Path directory;
	private static Configuration configuration;

	//Client-sided

	public static boolean reloadSoundSystemKeyBind;
	public static boolean moveBucketCreativeTab;
	public static boolean contributorCapes;

	//General

	public static boolean moreRomanNumerals;
	public static boolean ocelotsCanBeHealed;
	public static boolean sleepTweaks;

	//World

	public static final int DEFAULT_VOID_WORLD_TYPE_Y_SPAWN = 17;

	public static boolean realisticWorldType;
	public static boolean voidWorldType;
	public static boolean voidIslandsWorldType;
	public static int voidWorldTypeYSpawn;
	public static String voidWorldTypeBlock;
	public static int voidWorldTypeBlockMeta;
	public static String voidWorldTypeBiome;
	public static int voidIslandsWorldTypeChunkRarity;

	//Squids

	public static final int DEFAULT_SQUID_SPAWN_LIMIT_RADIUS = 64;
	public static final int SQUID_SPAWNING_DISABLED = -1;
	public static final int DEFAULT_SQUID_CHUNK_LIMIT = 12;
	public static final int SQUID_CHUNK_LIMIT_DISABLED = 0;
	public static final int SQUID_SPAWN_LIMIT_RADIUS_DISABLED = 0;
	public static final int DEFAULT_MAX_SQUID_PACK_SIZE = 2;
	public static final int DEFAULT_SQUID_PACK_SIZE = 0;

	public static int squidSpawnLimitRadius;
	public static int squidChunkLimit;
	public static int maxSquidPackSize;

	//Commands

	public static boolean deletegameruleCommand;
	public static boolean hungerCommand;
	public static boolean giveCommandAcceptsIntegerIDs;
	public static boolean rtreloadCommand;

	//Hunger

	public static final int RESET_HUNGER_ON_RESPAWN = 0;
	public static final int DONT_RESET_HUNGER_ON_RESPAWN = 1;
	public static final int DONT_RESET_HUNGER_IF_KEEPINVENTORY = 2;
	public static final int DONT_RESET_HUNGER_IF_KEEPINVENTORY_AND_NOT_CREATIVE = 3;
	public static final int DEFAULT_MINIMUM_HUNGER_LEVEL_ON_RESPAWN = 3;

	public static int hungerRespawnBehavior;
	public static int minimumHungerLevelOnRespawn;

	private static final Map<String, String> clientSidedFlags = new HashMap<>();
	private static final Map<String, String> generalFlags = new HashMap<>();
	private static final Map<String, String> worldFlags = new HashMap<>();
	private static final List<Entry> worldEntries = new ArrayList<>();
	private static final List<Entry> squidEntries = new ArrayList<>();
	private static final Map<String, String> commandFlags = new HashMap<>();
	private static final List<Entry> hungerEntries = new ArrayList<>();

	private static final List<String> needsMcRestart = new ArrayList<>();
	private static final List<String> needsWorldRestart = new ArrayList<>();

	private static final Set<String> usedCategories = new HashSet<>();
	private static final Set<String> usedKeys = new HashSet<>();

	private static final class Entry {
		final String key;
		final String comment;
		final String defaultString;
		final int defaultInt;
		final int minValue;
		final int maxValue;

		Entry(String key, String comment, String defaultValue) {
			this.key = key;
			this.comment = comment;
			defaultString = defaultValue;
			defaultInt = 0;
			minValue = 0;
			maxValue = 0;
		}

		Entry(String key, String comment, int defaultValue) {
			this(key, comment, defaultValue, Integer.MIN_VALUE);
		}

		Entry(String key, String comment, int defaultValue, int minValue) {
			this(key, comment, defaultValue, minValue, Integer.MAX_VALUE);
		}

		Entry(String key, String comment, int defaultValue,
				int minValue, int maxValue) {
			this.key = key;
			this.comment = comment;
			defaultString = null;
			defaultInt = defaultValue;
			this.minValue = minValue;
			this.maxValue = maxValue;
		}
	}

	static void initialize(FMLPreInitializationEvent event) throws Exception {
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

	@SubscribeEvent
	public static void onConfigurationChanged(OnConfigChangedEvent event) throws Exception {
		configuration.save();
		reloadConfiguration();
	}

	public static void reloadConfiguration() throws Exception {
		configuration.load();

		if(!clientSidedFlags.isEmpty()) {
			readAll();
			return;
		}

		//Client sided

		clientSidedFlags.put("reloadSoundSystemKeyBind", "Self explanatory.");
		clientSidedFlags.put("moveBucketCreativeTab",
				"Move the bucket to the Tools tab in creative mode.");

		clientSidedFlags.keySet().forEach(key -> needsMcRestart.add(key));

		clientSidedFlags.put("contributorCapes", "Self explanatory.");

		//General

		generalFlags.put("moreRomanNumerals", "Self explanatory.");
		generalFlags.put("ocelotsCanBeHealed", "Ocelots can be healed with fish.");
		generalFlags.put("sleepTweaks", "Players can sleep " +
				"around non-aggressive zombie pigmen and mobs with custom names. On 1.10, " +
				"adds a \"bed is too far away\" message. Server-sided.");

		//World

		worldFlags.put("realisticWorldType", "Enables the Realistic world type. Name: realistic");
		worldFlags.put("voidWorldType", "Enables the Void world type. Name: void");
		worldFlags.put("voidIslandsWorldType",
				"Enables the Void Islands world type. Name: voidislands");

		worldFlags.keySet().forEach(key -> needsMcRestart.add(key));

		worldEntries.add(new Entry("voidWorldTypeYSpawn", "The Y coordinate of the default " +
				"spawn point in a Void world.", DEFAULT_VOID_WORLD_TYPE_Y_SPAWN, 1, 255));
		worldEntries.add(new Entry("voidWorldTypeBlock", "What block should be placed in the " +
				"Void world for players to stand on.", "minecraft:glass"));
		worldEntries.add(new Entry("voidWorldTypeBlockMeta",
				"The damage value for voidWorldTypeBlock.", 0));
		worldEntries.add(new Entry("voidWorldTypeBiome", "What biome the Void world type uses. " +
				"Set to an empty string to use Minecraft's default behavior.",
				"minecraft:plains"));

		//Squids

		squidEntries.add(new Entry("squidSpawnLimitRadius", "Disables squid " +
				"spawning if a player is not within the specified radius. Set to 0 to " +
				"disable this limit.", DEFAULT_SQUID_SPAWN_LIMIT_RADIUS, 0));
		squidEntries.add(new Entry("squidChunkLimit", "Limits the amount of squids allowed " +
				"in a chunk. Set to 0 to disable squid spawning, and set to -1 to disable this " +
				"limit.", DEFAULT_SQUID_CHUNK_LIMIT, SQUID_CHUNK_LIMIT_DISABLED));
		squidEntries.add(new Entry("maxSquidPackSize", "The maximum number of squids that can " +
				"be spawned in a pack. Set to 0 to use vanilla's default behavior.",
				DEFAULT_MAX_SQUID_PACK_SIZE, DEFAULT_SQUID_PACK_SIZE));

		//Commands

		commandFlags.put("deletegameruleCommand", "Self explanatory.");
		commandFlags.put("hungerCommand", "Sets a player's hunger.");
		commandFlags.put("giveCommandAcceptsIntegerIDs", "Self explanatory.");
		commandFlags.put("rtreloadCommand", "Reloads this configuration.");

		commandFlags.keySet().forEach(entry -> needsWorldRestart.add(entry));

		//Hunger

		hungerEntries.add(new Entry("hungerRespawnBehavior", "0 = hunger resets on respawn; " +
				"1 = hunger doesn't reset on respawn; 2 = hunger doesn't reset on respawn if " +
				"keepInventory is true; 3 = 2, but only if the player is not in creative",
				DONT_RESET_HUNGER_IF_KEEPINVENTORY_AND_NOT_CREATIVE,
				RESET_HUNGER_ON_RESPAWN, DONT_RESET_HUNGER_IF_KEEPINVENTORY_AND_NOT_CREATIVE));
		hungerEntries.add(new Entry("minimumHungerLevelOnRespawn", "If hungerRespawnBehavior " +
				"is active this sets the minimum hunger on respawn so a player doesn't spawn " +
				"with 0 hunger.", DEFAULT_MINIMUM_HUNGER_LEVEL_ON_RESPAWN, 0));

		readAll();
		configuration.save();
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

		for(Map.Entry<String, JsonElement> entry : object.entrySet()) {
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
		for(Map.Entry<String, JsonElement> entry : object.entrySet()) {
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

	public static List<IConfigElement> getConfigElements() {
		final List<IConfigElement> elements = new ArrayList<>();
		configuration.getCategoryNames().forEach(category -> elements.add(new ConfigElement(
				configuration.getCategory(category))));
		return elements;
	}

	private static boolean requiresRestart(String category, String key, String description) {
		final Property property = configuration.get(category, key, true, description);
		property.setRequiresMcRestart(true);
		return property.getBoolean();
	}

	private static void readAll() throws Exception {
		readFlags("clientsided", clientSidedFlags);
		readFlags("world", worldFlags);
		readEntries("world", worldEntries);
		readEntries("squids", squidEntries);
		readFlags("commands", commandFlags);
		readEntries("hunger", hungerEntries);
		removeObsoleteKeys();
	}

	private static void readFlags(String category, Map<String, String> flags) throws Exception {
		usedCategories.add(category);

		for(String key : flags.keySet()) {
			final Property property = configuration.get(category, key, true, flags.get(key));
			checkRestartNeeded(property);
			ConfigurationHandler.class.getDeclaredField(key).set(null, property.getBoolean());

			usedKeys.add(key);
		}
	}

	private static void readEntries(String category, List<Entry> entries) throws Exception {
		usedCategories.add(category);

		for(Entry entry : entries) {
			if(entry.defaultString != null) {
				final Property property = configuration.get(category, entry.key,
						entry.defaultString, entry.comment);

				checkRestartNeeded(property);

				ConfigurationHandler.class.getDeclaredField(entry.key).set(null,
						property.getString());
			} else {
				final Property property = configuration.get(category, entry.key,
						entry.defaultInt, entry.comment, entry.minValue, entry.maxValue);

				checkRestartNeeded(property);

				ConfigurationHandler.class.getDeclaredField(entry.key).set(null,
						property.getInt());
			}

			usedKeys.add(entry.key);
		}
	}

	private static void checkRestartNeeded(Property property) {
		if(needsMcRestart.contains(property.getName())) {
			property.setRequiresMcRestart(true);
		}
		if(needsWorldRestart.contains(property.getName())) {
			property.setRequiresWorldRestart(true);
		}
	}

	private static void removeObsoleteKeys() {
		for(String categoryName : configuration.getCategoryNames()) {
			final ConfigCategory category = configuration.getCategory(categoryName);

			if(!usedCategories.contains(categoryName)) {
				configuration.removeCategory(category);
			}

			for(ConfigCategory key : category.getChildren()) {
				if(!usedKeys.contains(key.getName())) {
					category.remove(key);
				}
			}
		}
	}
}
