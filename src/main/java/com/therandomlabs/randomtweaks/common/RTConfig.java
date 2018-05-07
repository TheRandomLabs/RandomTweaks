package com.therandomlabs.randomtweaks.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.MalformedJsonException;
import com.therandomlabs.randomtweaks.util.Alignment;
import com.therandomlabs.randomtweaks.util.Compat;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

@EventBusSubscriber
@Config(modid = RandomTweaks.MODID, name = RandomTweaks.MODID + "/" + RandomTweaks.MODID)
@Config.LangKey("randomtweaks.config.title")
public class RTConfig {
	public static class AutoThirdPerson {
		@Config.Comment("Whether auto third person is enabled.")
		public boolean enabled = true;

		@Config.Comment("Whether auto third person is enabled when flying with elytra.")
		public boolean elytra = true;

		@Config.Comment("Whether auto third person is enabled when riding a horse.")
		public boolean horse = true;

		@Config.Comment("Whether auto third person is enabled when riding a pig.")
		public boolean pig = true;

		@Config.Comment("Whether auto third person is enabled when riding a boat.")
		public boolean boat = true;

		@Config.Comment("Whether auto third person is enabled when riding a minecart.")
		public boolean minecart = true;
	}

	public static class Client {
		@Config.RequiresMcRestart
		@Config.Comment("Moves the Bucket to the Tools creative tab.")
		public boolean moveBucketCreativeTab = true;

		@Config.RequiresMcRestart
		@Config.Comment("Moves spawn eggs to their own creative tab.")
		public boolean spawnEggsCreativeTab = true;

		@Config.RequiresMcRestart
		@Config.Comment("Enables contributor capes.")
		public boolean contributorCapes = true;

		@Config.Comment("Disables the Wither spawn sound.")
		public boolean disableWitherSpawnSound;

		@Config.Comment("Disables the Ender Dragon death sound.")
		public boolean disableEnderDragonDeathSound;

		@Config.Comment("Stops potion effects from moving GUIs to the right.")
		public boolean disablePotionShift = true;

		@Config.Comment("Removes underwater fog.")
		public boolean clearWater = true;

		@Config.Comment("Whether to enable stepup auto jump.")
		public boolean stepup;

		@Config.Comment("Whether stepup auto jump is enabled by default.")
		public boolean stepupEnabledByDefault;
	}

	public static class Commands {
		@Config.RequiresWorldRestart
		@Config.Comment("Enables the /deletegamerule command.")
		public boolean deletegamerule = true;

		@Config.RequiresWorldRestart
		@Config.Comment("Enables the /hunger command, which sets a player's hunger level.")
		public boolean hunger = true;

		@Config.RequiresWorldRestart
		@Config.Comment("Allows /give to accept integer IDs, amounts higher than 64, and " +
				"ore dictionary names prefixed by \"ore:\".")
		public boolean giveTweaks = true;

		@Config.RequiresWorldRestart
		@Config.Comment("Changes the /help command to make sure that sorting commands " +
				"doesn't cause an error.")
		public boolean helpTweaks = true;

		@Config.RequiresWorldRestart
		@Config.Comment("Enables the /rtreload command, which reloads this configuration.")
		public boolean rtreload = true;

		@Config.RequiresMcRestart
		@Config.Comment("Enables the /rtreloadclient command, which is the client-sided " +
				"version of /rtreload.")
		public boolean rtreloadclient = true;

		@Config.RequiresMcRestart
		@Config.Comment("Enables the client-sided /disconnect command, which leaves the " +
				"current world.")
		public boolean disconnect = true;

		@Config.Comment("Whether the shortened versions of the gamemode commands (/gms, /gmc, " +
				"/gma and /gmsp) should be replaced by the longer ones before being sent. " +
				"This is client-sided and does not support tab completion. This does not work in " +
				"1.10.")
		public boolean shortGamemodeCommands;
	}

	public static class Ding {
		@Config.Comment("Disables this feature if iChun's Ding is installed.")
		public boolean disableIfDingIsInstalled = true;

		@Config.Comment("The names of the sounds to play when Minecraft starts. " +
				"Leave this empty to disable it.")
		public String[] soundNames = new String[] {
				"entity.experience_orb.pickup"
		};

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
		@Config.Comment("Enables Roman numerals from -32768 to 32767.")
		public boolean moreRomanNumerals = true;

		@Config.Comment("Ocelots can be healed with fish.")
		public boolean ocelotsCanBeHealed = true;

		@Config.Comment("Players can sleep around non-aggressive zombie pigmen " +
				"(this is a default feature in 1.12) and mobs with custom names. On 1.10, " +
				"adds the \"bed is too far away\" message present in later versions of Minecraft.")
		public boolean sleepTweaks = true;

		@Config.RangeDouble(min = 0.0, max = 1024.0)
		@Config.Comment("Sets the base attack speed for players. Set this to 16.0 or higher " +
				"to remove the attack cooldown. 4.0 is the default value.")
		public double attackSpeed = 4.0;

		@Config.Comment("Prevents pets from being attacked by their owners " +
				"(unless they're sneaking).")
		public boolean protectPetsFromOwners = true;

		@Config.Comment("Prevents pets from being attacked by their owner's other pets.")
		public boolean protectPetsFromOtherPets = true;

		@Config.Comment("Replaces NuclearCraft Sulfur drops with Thermal Expansion Sulfur.")
		public boolean dropTESulfur;

		@Config.Comment("Disables the cumulative anvil cost.")
		public boolean disableCumulativeAnvilCost = true;

		@Config.Comment("Allows skeleton arrows to be picked up.")
		public boolean pickupSkeletonArrows;
	}

	public static class Keybinds {
		@Config.RequiresMcRestart
		@Config.Comment("Enables the Reload Sound System keybind.")
		public boolean reloadSoundSystem = true;

		@Config.RequiresMcRestart
		@Config.Comment("Enables the Clear Chat keybind.")
		public boolean clearChat = true;

		@Config.RequiresMcRestart
		@Config.Comment("Enables the Noclip keybind, which toggles between /gamemode c " +
				"and /gamemode sp.")
		public boolean noclip = true;

		@Config.Comment("Enables a keybind to disable FoV changes.")
		public boolean toggleFoVChanges = true;

		@Config.Comment("Whether FoV changes are enabled by default.")
		public boolean fovChangesEnabledByDefault = true;

		@Config.Comment("Whether a status message should be displayed when FoV changes are " +
				"toggled.")
		public boolean fovChangesStatusMessage = true;
	}

	public static class OceanFloor {
		@Config.RequiresMcRestart
		@Config.Comment("Enables the ocean floor worldgen.")
		public boolean enabled = true;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.Comment("The amount of clay in a vein.")
		public int clayVeinSize = 32;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.Comment("The clay spawn chance.")
		public int clayChance = 65;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.Comment("The amount of sand in a vein.")
		public int sandVeinSize = 22;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.Comment("The sand spawn chance.")
		public int sandChance = 45;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.Comment("The amount of dirt in a vein.")
		public int dirtVeinSize = 18;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.Comment("The dirt spawn chance.")
		public int dirtChance = 30;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.Comment("The minimum Y value.")
		public int minY = 20;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.Comment("The maximum Y value.")
		public int maxY = 128;
	}

	public static class PlayerHeadDrops {
		@Config.Comment("Whether players should drop their heads when they die.")
		public boolean enabled = true;

		@Config.RangeDouble(min = 0.0, max = 1.0)
		@Config.Comment("The normal player head drop chance.")
		public double normalChance = 1.0;

		@Config.RangeDouble(min = 0.0, max = 1.0)
		@Config.Comment("The player head drop chance when a player is killed by another player.")
		public double chanceWhenKilledByPlayer = 1.0;

		@Config.RangeDouble(min = 0.0, max = 1.0)
		@Config.Comment("The player head drop chance when a player is killed by a charged creeper.")
		public double chanceWhenKilledByChargedCreeper = 1.0;
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

		@Config.Comment("The biome of a Void world. Leave this empty to randomize the biomes.")
		public String voidWorldBiome = "minecraft:plains";

		@Config.RequiresMcRestart
		@Config.Comment("Enables the Void Islands world type. Name: voidislands")
		public boolean voidIslandsWorldType = true;

		@Config.Comment("The biome of the void chunks of a Void Islands world. " +
				"Leave this empty to randomize the biomes.")
		public String voidIslandsWorldBiome = "minecraft:plains";

		@Config.RangeInt(min = 2)
		@Config.Comment("The rarity of non-empty chunks in a Void Islands world. " +
				"If n, there is a 1 in n chance of a chunk being non-empty.")
		public int voidIslandsChunkRarity = 10;
	}

	public static class Respawn {
		@Config.Comment("What happens to a player's hunger when they respawn.")
		public RespawnHandler.HungerBehavior hungerBehavior =
				RespawnHandler.HungerBehavior.RESET_UNLESS_KEEPINVENTORY;

		@Config.RangeInt(min = 0)
		@Config.Comment("The minimum hunger level on respawn.")
		public int minimumHungerLevel = 3;

		@Config.Comment("Enables punishments on death if keepInventory is enabled so it's not " +
				"too overpowered. All XP and 3 hunger points are removed. This " +
				"overrides hungerBehavior if keepInventory is true.")
		public boolean deathPunishmentsIfKeepInventory;
	}

	public static class Squids {
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

	public static class TimeOfDay {
		@Config.Comment("Enables the time of day overlay.")
		public boolean enabled = true;

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

		@Config.Comment("The alignment of the time of day overlay.")
		public Alignment alignment = Alignment.TOPLEFT;

		@Config.Comment("The X offset of the time of day overlay.")
		public int x = 0;

		@Config.Comment("The Y offset of the time of day overlay.")
		public int y = 0;
	}

	public static class DefaultGamerules {
		public static void create() throws IOException {
			final Path path = Paths.get("config", RandomTweaks.MODID, "defaultgamerules.json");

			if(Files.exists(path)) {
				Files.move(path, Paths.get(path.toString() + "_backup" + System.nanoTime()));
			}

			Files.write(path, Arrays.asList(
					"//Example configuration - game does not need to be restarted when changing this",
					"{",
					"//\t\"commandBlockOutput\": false, //These are for all game modes",
					"//\t\"keepInventory\": true,",
					"//\t\"1:flat,void\": { //Creative flat/void world. Game modes and world types " +
							"can be separated with commas (no spaces). These are the same as the " +
							"world types in command.properties.",
					"//\t\t\"doDaylightCycle\": false,",
					"//\t\t\"doWeatherCycle\": false,",
					"//\t\t\"doMobSpawning\": false,",
					"//\t\t\"rtWorldBorderSize\": 10000 //Sets the world border (in blocks) " +
							"from chunk (0, 0)",
					"//\t}",
					"}"
			));
		}

		public static Map<String, String> get(int gamemode, String worldType) throws IOException {
			final Path path = getJson("defaultgamerules");

			if(!Files.exists(path)) {
				create();
				return Collections.emptyMap();
			}

			JsonObject object;
			try {
				object = readJson(path);
			} catch(MalformedJsonException ex) {
				ex.printStackTrace();
				//WorldCreateHandler sends a command message if this is null saying that the
				//JSON was invalid, so there's no need to crash the game
				return null;
			}

			final Map<String, String> gamerules = new HashMap<>();

			for(Map.Entry<String, JsonElement> entry : object.entrySet()) {
				if(entry.getValue().isJsonObject() &&
						matchesGamemodeAndWorldType(entry.getKey(), gamemode, worldType)) {
					putGamerules(gamerules, entry.getValue().getAsJsonObject());
					continue;
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

		//Format: comma-separated integer gamemodes (optional):comma-separated world types (optional)
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
	}

	public static class Data {
		private static Data data;

		public Map<String, Boolean> timeOfDayOverlay;
		public boolean stepup;
		public boolean fovChanges;

		public static Data get() {
			if(data == null) {
				try {
					final Path path = getJson("data");

					if(Files.exists(path)) {
						try {
							data = new Gson().fromJson(readFile(path), Data.class);
						} catch(MalformedJsonException ex) {
							RandomTweaks.LOGGER.error("Replacing invalid data file...");
						}
					}
				} catch(IOException ex) {
					Utils.crashReport("Error while loading RandomTweaks data", ex);
				}

				if(data == null) {
					data = new Data();

					data.timeOfDayOverlay = new HashMap<>();
					data.stepup = client.stepupEnabledByDefault;
					data.fovChanges = keybinds.fovChangesEnabledByDefault;
				}

				if(data.timeOfDayOverlay == null) {
					data.timeOfDayOverlay = new HashMap<>();
				}
			}

			return data;
		}

		public static void save() {
			//Ensure non-null
			get();

			try {
				Files.write(getJson("data"), Collections.singletonList(new Gson().toJson(data)));
			} catch(IOException ex) {
				Utils.crashReport("Error while saving RandomTweaks data", ex);
			}
		}
	}

	@Config.Comment("Auto third person")
	public static AutoThirdPerson autoThirdPerson = new AutoThirdPerson();
	@Config.Comment("Client-sided")
	public static Client client = new Client();
	@Config.Comment("Commands")
	public static Commands commands = new Commands();
	@Config.Comment("Ding")
	public static Ding ding = new Ding();
	@Config.Comment("General")
	public static General general = new General();
	@Config.Comment("Keybinds")
	public static Keybinds keybinds = new Keybinds();
	@Config.Comment("Ocean floor")
	public static OceanFloor oceanFloor = new OceanFloor();
	@Config.Comment("Player head drops")
	public static PlayerHeadDrops playerHeadDrops = new PlayerHeadDrops();
	@Config.Comment("World")
	public static World world = new World();
	@Config.Comment("Respawn behavior")
	public static Respawn respawn = new Respawn();
	@Config.Comment("Squid spawning")
	public static Squids squids = new Squids();
	@Config.Comment("Time of day overlay")
	public static TimeOfDay timeofday = new TimeOfDay();

	public static Path getConfig(String name) throws IOException {
		final Path path = Paths.get("config", RandomTweaks.MODID, name);
		final Path parent = path.getParent();
		if(parent != null) {
			if(Files.exists(parent) && !Files.isDirectory(parent)) {
				Files.delete(parent);
			}

			Files.createDirectories(parent);
		}
		return path;
	}

	public static Path getJson(String name) throws IOException {
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
		if(event.getModID().equals(RandomTweaks.MODID)) {
			reloadConfig();
		}
	}

	public static void reloadConfig() {
		Compat.syncConfig(RandomTweaks.MODID, Config.Type.INSTANCE);
		Data.data = null;
	}

	static void preInit() {
		try {
			Files.deleteIfExists(Paths.get("config", "randomtweaks.cfg"));
			Files.deleteIfExists(getConfig("dontresetconfig.txt"));
			Files.deleteIfExists(getJson("logfilters"));
			Files.deleteIfExists(getJson("timeofdayoverlayworlds"));
		} catch(IOException ex) {
			Utils.crashReport("Failed to delete old RandomTweaks files", ex);
		}

		reloadConfig();
	}
}
