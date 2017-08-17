package com.therandomlabs.randomtweaks.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.lang3.StringUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
@Config(modid = RandomTweaks.MODID, name = RandomTweaks.MODID + "/" + RandomTweaks.MODID)
@Config.LangKey("randomtweaks.config.title")
public class RTConfig {
	public static class Client {
		@Config.RequiresMcRestart
		@Config.Comment("Enables the Reload Sound System keybind.")
		public boolean reloadSoundSystemKeybind = true;

		@Config.RequiresMcRestart
		@Config.Comment("Moves the Bucket to the Tools creative tab.")
		public boolean moveBucketCreativeTab = true;

		@Config.RequiresMcRestart
		@Config.Comment("Moves the spawn eggs to their own creative tab.")
		public boolean spawnEggsCreativeTab = true;

		@Config.RequiresMcRestart
		@Config.Comment("Enables Contributor Capes.")
		public boolean contributorCapes = true;
	}

	public static class Ding {
		@Config.Comment("The name of the sound to play when Minecraft starts. " +
				"Leave this empty to disable it.")
		public String soundName = "entity.experience_orb.pickup";

		@Config.RangeDouble(min = 0.0, max = 10.0)
		@Config.Comment("The pitch of the sound to play when Minecraft starts.")
		public double soundPitch = 1.0;

		@Config.Comment("The name of the sound to play when a world loads." +
				"Leave this empty to disable it.")
		public String worldSoundName = "entity.experience_orb.pickup";

		@Config.RangeDouble(min = 0.0, max = 10.0)
		@Config.Comment("The pitch of the sound to play when a world starts.")
		public double worldSoundPitch = 1.0;
	}

	public static class World {
		@Config.RequiresMcRestart
		@Config.Comment("Disables the Realistic world type if Quark is detected.")
		public boolean disableRealisticWorldTypeWithQuark = true;

		@Config.RequiresMcRestart
		@Config.Comment("Enables the Realistic world type. Name: realistic")
		public boolean realisticWorldType;

		@Config.RequiresMcRestart
		@Config.Comment("Enables the Void world type. Name: void")
		public boolean voidWorldType;

		@Config.RangeInt(min = 1, max = 255)
		@Config.Comment("The Y coordinate of the default spawn point in a Void world.")
		public int voidWorldYSpawn = 17;

		@Config.Comment("The block placed in a Void world for players to stand on.")
		public String voidWorldBlock = "minecraft:glass";

		@Config.Comment("The meta value for voidWorldBlock.")
		public int voidWorldBlockMeta;

		@Config.Comment("The biome of a Void world.")
		public String voidWorldBiome = "minecraft:plains";

		@Config.RequiresMcRestart
		@Config.Comment("Enables the Void Islands world type. Name: voidislands")
		public boolean voidIslandsWorldType;

		@Config.RangeInt(min = 2)
		@Config.Comment("The rarity of non-empty chunks in a Void Islands world. " +
				"If n, there is a 1 in n chance of a chunk being non-empty.")
		public int voidIslandsChunkRarity = 10;
	}

	public static class Squids {
		public static final int RADIUS_LIMIT_DISABLED = 0;
		public static final int CHUNK_LIMIT_DISABLED = -1;
		public static final int SQUID_SPAWNING_DISABLED = 0;
		public static final int VANILLA_PACK_SIZE = 0;

		@Config.RangeInt(min = 0)
		@Config.Comment("Disables squid spawning when a player is not within the specified " +
				"radius. Set to 0 to disable this limit.")
		public int spawnRadiusLimit = 64;

		@Config.RangeInt(min = -1)
		@Config.Comment("The amount of squids allowed in one chunk. 0 disables squid spawning, " +
				"and -1 disables this limit.")
		public int chunkLimit = 12;

		@Config.RangeInt(min = 0)
		@Config.Comment("The maximum number of squids that can be spawned in a pack. " +
				"Set to 0 to use vanilla behavior.")
		public int maxPackSize = 2;
	}

	public static class Commands {
		@Config.RequiresMcRestart
		@Config.Comment("Enables the /deletegamerule command.")
		public boolean deletegamerule;

		@Config.RequiresMcRestart
		@Config.Comment("Enables the /hunger command, which sets a player's hunger level.")
		public boolean hunger;

		@Config.RequiresMcRestart
		@Config.Comment("Allows /give to accept integer IDs and amounts higher than 64.")
		public boolean giveTweaks;

		@Config.RequiresMcRestart
		@Config.Comment("Enables the /rtreload command, which reloads this configuration.")
		public boolean rtreload;
	}

	public static class Hunger {
		public static final int RESET_ON_RESPAWN = 0;
		public static final int DONT_RESET_ON_RESPAWN = 1;
		public static final int RESET_UNLESS_KEEPINVENTORY = 2;
		public static final int RESET_UNLESS_KEEPINVENTORY_OR_CREATIVE = 3;

		@Config.RangeInt(min = 0, max = 3)
		@Config.Comment("0 = reset hunger on respawn; 2 = don't reset hunger on respawn; " +
				"2 = reset hunger on respawn unless keepInventory is true; 3 = reset hunger " +
				"on respawn unless keepInventory is true or the player is in creative mode")
		public int respawnBehavior;

		@Config.RangeInt(min = 0)
		@Config.Comment("The minimum hunger level on respawn.")
		public int minimumHungerLevel = 3;
	}

	//General
	@Config.RequiresMcRestart
	@Config.Comment("Enables more Roman numerals (Roman numerals from -32768 to 32767).")
	public static boolean moreRomanNumerals = true;

	@Config.Comment("Ocelots can be healed with fish.")
	public static boolean ocelotsCanBeHealed = true;

	@Config.Comment("Players can sleep around non-aggressive zombie pigmen and mobs with " +
			"custom names. On 1.10, adds the \"bed is too far away\" message present in " +
			"later versions of Minecraft.")
	public static boolean sleepTweaks = true;

	public static Client client = new Client();
	public static Ding ding = new Ding();
	public static World world = new World();
	public static Squids squids = new Squids();
	public static Commands commands = new Commands();
	public static Hunger hunger = new Hunger();

	private static final List<String> LOG_FILTER_KEYS = Arrays.asList(
			"disableLogging",
			"levelFilter",
			"nameFilter",
			"messageFilter",
			"classFilter",
			"threadFilter",
			"throwableClassFilter",
			"throwableMessageFilter"
	);
	private static boolean disableLogging;
	private static Map<String, Pattern> logFilters;

	static {
		loadLogFilters();
	}

	public static void createLogFilters() throws IOException {
		final Path path = Paths.get("logfilters.json");

		if(Files.exists(path)) {
			Files.move(path, Paths.get(path.toString() + "_backup" + System.nanoTime()));
		}

		Files.write(path, Arrays.asList(
				"{",
				"\t\"disableLogging\": false, //Set this to true to disable logging.",
				"\t\"levelFilter\": \"\", //A regex that matches the level. Example: TRACE|DEBUG",
				"\t\"nameFilter\": \"\", //A regex that matches the logger name. Example: ^FML$",
				"\t\"messageFilter\": \"\", //A regex that matches the message. " +
						"Example: ^Skipping bad option: lastServer: $",
				"\t\"classFilter\": \"\", //A regex that matches the caller class name. " +
						"No example yet.",
				"\t\"throwableClassFilter\": \"\", //A regex that matches the throwable class " +
						"name, if there is a throwable. " +
						"Example: ^java.lang.ArrayIndexOutOfBoundsException$",
				"\t\"throwableMessageFilter\": \"\", //A regex that matches the throwable's " +
						"message, if there is a throwable. No example yet.",
				"\t\"threadFilter\": \"\" //A regex that matches the thread name. " +
						"Example: ^Server thread$",
				"}"
		));
	}

	public static void loadLogFilters() {
		try {
			final Path path = Paths.get("logfilters.json");

			if(!Files.exists(path)) {
				createLogFilters();
			}

			logFilters = new HashMap<>();

			JsonObject object = null;
			try {
				object = readJson(path);
			} catch(JsonSyntaxException ex) {
				ex.printStackTrace();
				err("Invalid JSON. Resetting log filters...");

				createLogFilters();
				object = readJson(path);
			}

			for(String key : LOG_FILTER_KEYS) {
				if(!object.has(key)) {
					err("\"%s\" is a required value. Resetting log filters...", key);

					createLogFilters();
					object = readJson(path);
				}

				try {
					logFilters.put(key, Pattern.compile(object.get(key).getAsString()));
				} catch(PatternSyntaxException ex) {
					err("Invalid pattern specified for \"%s\". Resetting log filters...", key);

					createLogFilters();
					object = readJson(path);

					logFilters.put(key, Pattern.compile(""));
				}
			}

			if(!isBoolean(object.get("disableLogging"))) {
				err("\"disableLogging\" must be a boolean. Resetting log filters...");

				createLogFilters();
				object = readJson(path);
			}

			disableLogging = object.get("disableLogging").getAsBoolean();
		} catch(IOException ex) {
			throw new ReportedException(new CrashReport("Failed to read log filters", ex));
		}
	}

	public static boolean disableLogging() {
		return disableLogging;
	}

	public static Map<String, Pattern> logFilters() {
		return logFilters;
	}

	public static void createDefaultGamerules() throws IOException {
		final Path path = Paths.get("config", RandomTweaks.MODID, "defaultgamerules.json");

		if(Files.exists(path)) {
			Files.move(path, Paths.get(path.toString() + "_backup" + System.nanoTime()));
		}

		Files.write(path, Arrays.asList(
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
		final Path path = Paths.get("config", RandomTweaks.MODID, "defaultgamerules.json");

		if(!Files.exists(path)) {
			createDefaultGamerules();
			return Collections.emptyMap();
		}

		JsonObject object = null;
		try {
			object = readJson(path);
		} catch(MalformedJsonException ex) {
			//WorldCreateHandler sends a server message if this is null saying that the
			//JSON was invalid, so there's no need to crash the game
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

	public static JsonObject readJson(Path path) throws IOException {
		return new JsonParser().parse(
				StringUtils.join(
						Files.readAllLines(path),
						System.lineSeparator()
				)
		).getAsJsonObject();
	}

	public static boolean isString(JsonElement element) {
		return element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
	}

	public static boolean isBoolean(JsonElement element) {
		return element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean();
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(event.getModID().equals(RandomTweaks.MODID)) {
			reloadConfig();
		}
	}

	public static void reloadConfig() {
		ConfigManager.sync(RandomTweaks.MODID, Config.Type.INSTANCE);
		loadLogFilters();
	}

	private static void err(String message, Object... args) {
		System.err.printf("[" + RandomTweaks.MODID + "] " + message + System.lineSeparator(), args);
	}
}
