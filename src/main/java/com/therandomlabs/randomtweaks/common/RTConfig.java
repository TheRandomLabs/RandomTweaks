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
import com.google.gson.Gson;
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

	public static class Ding {
		@Config.Comment("Disables this feature if iChun's Ding is installed.")
		public boolean disableIfDingIsInstalled = true;

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

	public static class General {
		@Config.RequiresMcRestart
		@Config.Comment("Enables more Roman numerals (Roman numerals from -32768 to 32767).")
		public boolean moreRomanNumerals = true;

		@Config.Comment("Ocelots can be healed with fish.")
		public boolean ocelotsCanBeHealed = true;

		@Config.Comment("Players can sleep around non-aggressive zombie pigmen and mobs with " +
				"custom names. On 1.10, adds the \"bed is too far away\" message present in " +
				"later versions of Minecraft.")
		public boolean sleepTweaks = true;

		@Config.Comment("Do not change this value.")
		public int configVersion = -1;
	}

	public static class World {
		@Config.RequiresMcRestart
		@Config.Comment("Disables the Realistic world type if Quark is detected.")
		public boolean disableRealisticWorldTypeWithQuark = true;

		@Config.RequiresMcRestart
		@Config.Comment("Enables the Realistic world type. Name: realistic")
		public boolean realisticWorldType = true;

		@Config.RequiresMcRestart
		@Config.Comment("Enables the Void world type. Name: void")
		public boolean voidWorldType = true;

		@Config.RangeInt(min = 1, max = 255)
		@Config.Comment("The Y coordinate of the default spawn point in a Void world.")
		public int voidWorldYSpawn = 17;

		@Config.Comment("The block placed in a Void world for players to stand on.")
		public String voidWorldBlock = "minecraft:glass";

		@Config.Comment("The biome of a Void world.")
		public String voidWorldBiome = "minecraft:plains";

		@Config.RequiresMcRestart
		@Config.Comment("Enables the Void Islands world type. Name: voidislands")
		public boolean voidIslandsWorldType = true;

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

	public static class TimeOfDay {
		@Config.RequiresMcRestart
		@Config.Comment("Enables the time of day overlay keybind.")
		public boolean enableKeybind = true;

		@Config.Comment("Enables the time of day overlay by default.")
		public boolean enabledByDefault;

		@Config.Comment("Disables the time of day overlay if doDaylightCycle is false.")
		public boolean disableIfNoDaylightCycle = true;

		@Config.Comment("Disables the time of day overlay in Adventure Mode.")
		public boolean disableInAdventureMode = true;

		@Config.Comment("Enables 24-hour time.")
		public boolean twentyFourHourTime;

		public static final Map<String, Boolean> worlds = new HashMap<>();

		public static void loadWorlds() {
			try {
				worlds.clear();

				final Path path = getJson("timeofdayoverlayworlds");
				if(!Files.exists(path)) {
					return;
				}

				worlds.putAll(new Gson().fromJson(readFile(path), Map.class));
			} catch(IOException ex) {
				throw new ReportedException(
						new CrashReport("Failed to read time of day overlay worlds", ex));
			}
		}

		public static void saveWorlds() {
			try {
				Files.write(getJson("timeofdayoverlayworlds"),
						Arrays.asList(new Gson().toJson(worlds)));
			} catch(IOException ex) {
				throw new ReportedException(
						new CrashReport("Failed to save time of day overlay worlds", ex));
			}
		}
	}

	@Config.Comment("Client-sided (excluding Ding and the time of day overlay)")
	public static Client client = new Client();
	@Config.Comment("Commands")
	public static Commands commands = new Commands();
	@Config.Comment("Ding")
	public static Ding ding = new Ding();
	@Config.Comment("General")
	public static General general = new General();
	@Config.Comment("World")
	public static World world = new World();
	@Config.Comment("Squid spawning")
	public static Squids squids = new Squids();
	@Config.Comment("Hunger behavior on respawn")
	public static Hunger hunger = new Hunger();
	@Config.Comment("Time of day overlay")
	public static TimeOfDay timeofday = new TimeOfDay();

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
		TimeOfDay.loadWorlds();
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
		final Path path = getJson("defaultgamerules");

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
			if(entry.getValue().isJsonObject() &&
					matchesGamemodeAndWorldType(entry.getKey(), gamemode, worldType)) {
				putGamerules(gamerules, entry.getValue().getAsJsonObject());
			}

			gamerules.put(entry.getKey(), entry.getValue().toString());
		}

		return gamerules;
	}

	private static void putGamerules(Map<String, String> gamerules, JsonObject object) {
		for(Map.Entry<String, JsonElement> entry : object.entrySet()) {
			gamerules.put(entry.getKey(), entry.getValue().toString());
		}
	}

	//Format: comma-separated integer gamemodes (optional),comma-separated world types (optional)
	//Examples: 0,1:flat	realistic	2:void,flat
	public static boolean matchesGamemodeAndWorldType(String string, int gamemode,
			String worldType) {
		final String[] split = string.split(":");
		final String[] gamemodes = split[0].split(",");

		boolean gamemodeFound = false;

		for(String mode : gamemodes) {
			try {
				if(Integer.parseInt(mode) == gamemode) {
					gamemodeFound = true;
					break;
				}
			} catch(NumberFormatException ex) {
				if(split.length == 1 && mode.equals(worldType)) {
					//Then it's a world type, not a mode.
					return true;
				}
			}
		}

		if(!gamemodeFound) {
			return false;
		}

		if(split.length > 1) {
			for(String type : split[1].split(",")) {
				if(type.equals(worldType)) {
					return true;
				}
			}
		}

		return false;
	}

	public static Path getConfig(String name) {
		return Paths.get("config", RandomTweaks.MODID, name);
	}

	public static Path getJson(String name) {
		return getConfig(name + ".json");
	}

	public static String readFile(Path path) throws IOException {
		return StringUtils.join(Files.readAllLines(path), System.lineSeparator());
	}

	public static JsonObject readJson(Path path) throws IOException {
		return new JsonParser().parse(readFile(path)).getAsJsonObject();
	}

	public static boolean isString(JsonElement element) {
		return element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
	}

	public static boolean isBoolean(JsonElement element) {
		return element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean();
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		System.out.println(event.getModID() + " " + event.getConfigID());
		if(event.getModID().equals(RandomTweaks.MODID)) {
			reloadConfig();
		}
	}

	public static void reloadConfig() {
		ConfigManager.sync(RandomTweaks.MODID, Config.Type.INSTANCE);
		loadLogFilters();
		TimeOfDay.loadWorlds();
	}

	static void preInit() {
		if(general.configVersion < 9) {
			try {
				Files.deleteIfExists(getConfig("randomtweaks.cfg"));
				Files.deleteIfExists(getConfig("../randomtweaks.cfg"));
				Files.deleteIfExists(getConfig("dontresetconfig.txt"));
			} catch(IOException ex) {
				throw new ReportedException(new CrashReport("Failed to reset config", ex));
			}

			general.configVersion = 9;
			reloadConfig();
		}
	}

	private static void err(String message, Object... args) {
		System.err.printf("[" + RandomTweaks.MODID + "] " + message + System.lineSeparator(), args);
	}
}
