package com.therandomlabs.randomtweaks.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.MalformedJsonException;
import com.therandomlabs.randomtweaks.common.RespawnHandler;
import com.therandomlabs.randomtweaks.util.Alignment;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

@Mod.EventBusSubscriber(modid = RandomTweaks.MODID)
@Config(modid = RandomTweaks.MODID, name = RandomTweaks.MODID + "/" + RandomTweaks.MODID)
@Config.LangKey("randomtweaks.config.title")
public class RTConfig {
	public static class AutoThirdPerson {
		@Config.LangKey("randomtweaks.config.autoThirdPerson.enabled")
		@Config.Comment("Whether auto third person is enabled.")
		public boolean enabled = true;

		@Config.LangKey("randomtweaks.config.autoThirdPerson.elytra")
		@Config.Comment("Whether auto third person is enabled when flying with elytra.")
		public boolean elytra = true;

		@Config.LangKey("randomtweaks.config.autoThirdPerson.horse")
		@Config.Comment("Whether auto third person is enabled when riding a horse.")
		public boolean horse = Utils.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.autoThirdPerson.pig")
		@Config.Comment("Whether auto third person is enabled when riding a pig.")
		public boolean pig = Utils.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.autoThirdPerson.boat")
		@Config.Comment("Whether auto third person is enabled when riding a boat.")
		public boolean boat = Utils.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.autoThirdPerson.minecart")
		@Config.Comment("Whether auto third person is enabled when riding a minecart.")
		public boolean minecart = Utils.IS_DEOBFUSCATED;
	}

	public static class Client {
		@Config.LangKey("randomtweaks.config.autoThirdPerson")
		@Config.Comment("Options related to the auto third person feature.")
		public AutoThirdPerson autoThirdPerson = new AutoThirdPerson();

		@Config.LangKey("randomtweaks.config.ding")
		@Config.Comment("Options related to the 'ding' feature.")
		public Ding ding = new Ding();

		@Config.LangKey("randomtweaks.config.keybinds")
		@Config.Comment("Options related to keybinds.")
		public Keybinds keybinds = new Keybinds();

		@Config.LangKey("randomtweaks.config.timeOfDay")
		@Config.Comment("Options related to the time of day overlay.")
		public TimeOfDay timeOfDay = new TimeOfDay();

		@Config.LangKey("randomtweaks.config.client.moveBucketCreativeTab")
		@Config.Comment("Moves the Bucket to the Tools creative tab.")
		public boolean moveBucketCreativeTab = true;

		@Config.LangKey("randomtweaks.config.client.spawnEggsCreativeTab")
		@Config.Comment("Moves spawn eggs to their own creative tab.")
		public boolean spawnEggsCreativeTab = true;

		@Config.RequiresWorldRestart
		@Config.LangKey("randomtweaks.config.client.contributorCapes")
		@Config.Comment("Enables contributor capes.")
		public boolean contributorCapes = true;

		@Config.LangKey("randomtweaks.config.client.disableWitherSpawnSound")
		@Config.Comment("Disables the Wither spawn sound.")
		public boolean disableWitherSpawnSound;

		@Config.LangKey("randomtweaks.config.client.disableEnderDragonDeathSound")
		@Config.Comment("Disables the Ender Dragon death sound.")
		public boolean disableEnderDragonDeathSound;

		@Config.LangKey("randomtweaks.config.client.disablePotionShift")
		@Config.Comment("Stops potion effects from moving GUIs to the right.")
		public boolean disablePotionShift = true;

		@Config.LangKey("randomtweaks.config.client.clearWater")
		@Config.Comment("Removes underwater fog.")
		public boolean clearWater = true;

		@Config.LangKey("randomtweaks.config.client.stepup")
		@Config.Comment("Whether to enable stepup auto jump.")
		public boolean stepup = Utils.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.client.stepupEnabledByDefault")
		@Config.Comment("Whether stepup auto jump is enabled by default.")
		public boolean stepupEnabledByDefault;
	}

	public static class Commands {
		@Config.RequiresWorldRestart
		@Config.LangKey("randomtweaks.config.commands.deletegamerule")
		@Config.Comment("Enables the /deletegamerule command.")
		public boolean deletegamerule = true;

		@Config.RequiresWorldRestart
		@Config.LangKey("randomtweaks.config.commands.hunger")
		@Config.Comment("Enables the /hunger command, which sets a player's hunger level.")
		public boolean hunger = true;

		@Config.RequiresWorldRestart
		@Config.LangKey("randomtweaks.config.commands.giveTweaks")
		@Config.Comment("Allows /give to accept integer IDs, amounts higher than 64, and " +
				"ore dictionary names prefixed by \"ore:\".")
		public boolean giveTweaks = true;

		@Config.RequiresWorldRestart
		@Config.LangKey("randomtweaks.config.commands.helpTweaks")
		@Config.Comment("Changes the /help command to make sure that sorting commands " +
				"doesn't cause an error.")
		public boolean helpTweaks = true;

		@Config.RequiresWorldRestart
		@Config.LangKey("randomtweaks.config.commands.rtreload")
		@Config.Comment("Enables the /rtreload command, which reloads this configuration.")
		public boolean rtreload = true;

		@Config.RequiresMcRestart
		@Config.LangKey("randomtweaks.config.commands.rtreloadclient")
		@Config.Comment("Enables the /rtreloadclient command, which is the client-sided " +
				"version of /rtreload.")
		public boolean rtreloadclient = true;

		@Config.RequiresMcRestart
		@Config.LangKey("randomtweaks.config.commands.disconnect")
		@Config.Comment("Enables the client-sided /disconnect command, which leaves the " +
				"current world.")
		public boolean disconnect = true;
	}

	public static class Ding {
		@Config.LangKey("randomtweaks.config.ding.soundNames")
		@Config.Comment("The names of the sounds to play when Minecraft starts. " +
				"Leave this empty to disable it.")
		public String[] soundNames = new String[] {
				"entity.experience_orb.pickup"
		};

		@Config.RangeDouble(min = 0.0, max = 10.0)
		@Config.LangKey("randomtweaks.config.ding.soundPitch")
		@Config.Comment("The pitch of the sound to play when Minecraft starts.")
		public double soundPitch = 1.0;

		@Config.LangKey("randomtweaks.config.ding.worldSoundNames")
		@Config.Comment("The name of the sound to play when a world loads." +
				"Leave this empty to disable it.")
		public String[] worldSoundNames = new String[] {
				"entity.experience_orb.pickup"
		};

		@Config.RangeDouble(min = 0.0, max = 10.0)
		@Config.LangKey("randomtweaks.config.ding.worldSoundPitch")
		@Config.Comment("The pitch of the sound to play when a world starts.")
		public double worldSoundPitch = 1.0;
	}

	public static class General {
		@Config.LangKey("randomtweaks.config.general.moreRomanNumerals")
		@Config.Comment("Enables Roman numerals from -32768 to 32767.")
		public boolean moreRomanNumerals = true;

		@Config.LangKey("randomtweaks.config.general.ocelotsCanBeHealed")
		@Config.Comment("Ocelots can be healed with fish.")
		public boolean ocelotsCanBeHealed = true;

		@Config.LangKey("randomtweaks.config.general.sleepTweaks")
		@Config.Comment("Allows players to sleep near mobs with custom names.")
		public boolean sleepTweaks = true;

		@Config.RangeDouble(min = 0.0, max = 1024.0)
		@Config.LangKey("randomtweaks.config.general.attackSpeed")
		@Config.Comment("Sets the base attack speed for players. Set this to 16.0 or higher " +
				"to remove the attack cooldown. 4.0 is the default value.")
		public double attackSpeed = Utils.IS_DEOBFUSCATED ? 24.0 : 4.0;

		@Config.LangKey("randomtweaks.config.general.deathPunishmentsIfKeepInventory")
		@Config.Comment("Enables punishments on death if keepInventory is enabled so it's not " +
				"too overpowered. All XP and 3 hunger points are removed. This " +
				"overrides hungerBehavior if keepInventory is true.")
		public boolean deathPunishmentsIfKeepInventory = Utils.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.general.protectPetsFromOwners")
		@Config.Comment("Prevents pets from being attacked by their owners " +
				"(unless they're sneaking).")
		public boolean protectPetsFromOwners = true;

		@Config.LangKey("randomtweaks.config.general.protectPetsFromOtherPets")
		@Config.Comment("Prevents pets from being attacked by their owner's other pets.")
		public boolean protectPetsFromOtherPets = true;

		@Config.LangKey("randomtweaks.config.general.disableCumulativeAnvilCost")
		@Config.Comment("Disables the cumulative anvil cost.")
		public boolean disableCumulativeAnvilCost = true;

		@Config.LangKey("randomtweaks.config.general.pickUpSkeletonArrows")
		@Config.Comment("Allows skeleton arrows to be picked up.")
		public boolean pickUpSkeletonArrows = Utils.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.general.requireFullCubeForSpawns")
		@Config.Comment("Disables mob spawning on non-full cubes.")
		public boolean requireFullCubeForSpawns = Utils.IS_DEOBFUSCATED;
	}

	public static class Hunger {
		@Config.LangKey("randomtweaks.config.hunger.behaviorOnRespawn")
		@Config.Comment("What happens to a player's hunger when they respawn.")
		public RespawnHandler.HungerBehavior behaviorOnRespawn =
				RespawnHandler.HungerBehavior.RESET_UNLESS_KEEP_INVENTORY;

		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.hunger.minimumRespawnHungerLevel")
		@Config.Comment("The minimum hunger level on respawn.")
		public int minimumRespawnHungerLevel = 3;

		@Config.RangeInt(min = 1)
		@Config.LangKey("randomtweaks.config.hunger.maximumHungerLevel")
		@Config.Comment("The maximum hunger level.")
		public int maximumHungerLevel = Utils.IS_DEOBFUSCATED ? 22 : 20;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("randomtweaks.config.hunger.saturationLimit")
		@Config.Comment("This value is added to the player's food level to calculate the maximum " +
				"saturation level.")
		public double saturationLimit = Utils.IS_DEOBFUSCATED ? 100.0 : 0.0;

		@Config.LangKey("randomtweaks.config.hunger.carryExcessHungerToSaturation")
		@Config.Comment("If this is enabled, any excess hunger level gained by eating will be " +
				"added to the saturation.")
		public boolean carryExcessHungerToSaturation = Utils.IS_DEOBFUSCATED;
	}

	public static class Keybinds {
		@Config.LangKey("randomtweaks.config.keybinds.reloadSoundSystem")
		@Config.Comment("Enables the Reload Sound System keybind.")
		public boolean reloadSoundSystem = true;

		@Config.LangKey("randomtweaks.config.keybinds.noclip")
		@Config.Comment("Enables the Noclip keybind, which toggles between /gamemode c " +
				"and /gamemode sp.")
		public boolean noclip = true;

		@Config.LangKey("randomtweaks.config.keybinds.toggleFoVChanges")
		@Config.Comment("Enables the Toggle FoV Changes keybind.")
		public boolean toggleFoVChanges = true;

		@Config.LangKey("randomtweaks.config.keybinds.fovChangesEnabledByDefault")
		@Config.Comment("Whether FoV changes are enabled by default.")
		public boolean fovChangesEnabledByDefault = true;

		@Config.LangKey("randomtweaks.config.keybinds.fovChangesStatusMessage")
		@Config.Comment("Whether a status message should be displayed when FoV changes are " +
				"toggled.")
		public boolean fovChangesStatusMessage = true;

		@Config.LangKey("randomtweaks.config.keybinds.toggleTimeOfDayOverlay")
		@Config.Comment("Enables the Toggle Time of Day Overlay keybind.")
		public boolean toggleTimeOfDayOverlay = true;
	}

	public static class OceanFloor {
		@Config.RequiresMcRestart
		@Config.LangKey("randomtweaks.config.oceanFloor.enabled")
		@Config.Comment("Enables the ocean floor worldgen.")
		public boolean enabled = true;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.oceanFloor.clayVeinSize")
		@Config.Comment("The amount of clay in a vein.")
		public int clayVeinSize = 32;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.oceanFloor.clayChance")
		@Config.Comment("The clay spawn chance.")
		public int clayChance = 65;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.oceanFloor.sandVeinSize")
		@Config.Comment("The amount of sand in a vein.")
		public int sandVeinSize = 22;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.oceanFloor.sandChance")
		@Config.Comment("The sand spawn chance.")
		public int sandChance = 45;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.oceanFloor.dirtVeinSize")
		@Config.Comment("The amount of dirt in a vein.")
		public int dirtVeinSize = 18;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.oceanFloor.dirtChance")
		@Config.Comment("The dirt spawn chance.")
		public int dirtChance = 30;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.oceanFloor.minY")
		@Config.Comment("The minimum Y coordinate.")
		public int minY = 20;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.oceanFloor.maxY")
		@Config.Comment("The maximum Y coordinate.")
		public int maxY = 128;
	}

	public static class PlayerHeadDrops {
		@Config.LangKey("randomtweaks.config.playerHeadDrops.enabled")
		@Config.Comment("Whether players should drop their heads when they die.")
		public boolean enabled = true;

		@Config.RangeDouble(min = 0.0, max = 1.0)
		@Config.LangKey("randomtweaks.config.playerHeadDrops.normalChance")
		@Config.Comment("The normal player head drop chance.")
		public double normalChance = 1.0;

		@Config.RangeDouble(min = 0.0, max = 1.0)
		@Config.LangKey("randomtweaks.config.playerHeadDrops.chanceWhenKilledByPlayer")
		@Config.Comment("The player head drop chance when a player is killed by another player.")
		public double chanceWhenKilledByPlayer = 1.0;

		@Config.RangeDouble(min = 0.0, max = 1.0)
		@Config.LangKey("randomtweaks.config.playerHeadDrops.chanceWhenKilledByChargedCreeper")
		@Config.Comment("The player head drop chance when a player is killed by a charged creeper.")
		public double chanceWhenKilledByChargedCreeper = 1.0;
	}

	public static class World {
		@Config.LangKey("randomtweaks.config.oceanFloor")
		@Config.Comment("Options related to ocean floor generation.")
		public OceanFloor oceanFloor = new OceanFloor();

		@Config.LangKey("randomtweaks.config.world.realisticWorldType")
		@Config.Comment("Enables the Realistic world type. Name: realistic")
		public boolean realisticWorldType = true;

		@Config.LangKey("randomtweaks.config.world.voidWorldType")
		@Config.Comment("Enables the Void world type. Name: void")
		public boolean voidWorldType = true;

		@Config.RangeInt(min = 1, max = 255)
		@Config.LangKey("randomtweaks.config.world.voidWorldYSpawn")
		@Config.Comment("The Y coordinate of the default spawn point in a Void world.")
		public int voidWorldYSpawn = 17;

		@Config.LangKey("randomtweaks.config.world.voidWorldBlock")
		@Config.Comment("The block placed in a Void world for players to stand on.")
		public String voidWorldBlock = "minecraft:glass";

		@Config.LangKey("randomtweaks.config.world.voidWorldBiome")
		@Config.Comment("The biome of a Void world. Leave this empty to randomize the biomes.")
		public String voidWorldBiome = "minecraft:plains";

		@Config.LangKey("randomtweaks.config.world.voidIslandsWorldType")
		@Config.Comment("Enables the Void Islands world type. Name: voidislands")
		public boolean voidIslandsWorldType = true;

		@Config.LangKey("randomtweaks.config.world.voidIslandsWorldBiome")
		@Config.Comment("The biome of the void chunks of a Void Islands world. " +
				"Leave this empty to randomize the biomes.")
		public String voidIslandsWorldBiome = "minecraft:plains";

		@Config.RangeInt(min = 2)
		@Config.LangKey("randomtweaks.config.world.voidIslandsChunkRarity")
		@Config.Comment("The rarity of non-empty chunks in a Void Islands world. " +
				"If n, there is a 1 in n chance of a chunk being non-empty.")
		public int voidIslandsChunkRarity = 10;
	}

	public static class Squids {
		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.squids.spawnRadiusLimit")
		@Config.Comment("Disables squid spawning when a player is not within the specified " +
				"radius. Set this to 0 to disable this limit.")
		public int spawnRadiusLimit = 64;

		@Config.RangeInt(min = -1)
		@Config.LangKey("randomtweaks.config.squids.chunkLimit")
		@Config.Comment("The amount of squids allowed in one chunk. Set this to 0 to disable " +
				"squid spawning, and set this to -1 to disable this limit.")
		public int chunkLimit = 12;

		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.squids.maxPackSize")
		@Config.Comment("The maximum number of squids that can be spawned in a pack. " +
				"Set to 0 to use vanilla behavior.")
		public int maxPackSize = 2;
	}

	public static class TimeOfDay {
		@Config.LangKey("randomtweaks.config.timeOfDay.enabled")
		@Config.Comment("Enables the overlay.")
		public boolean enabled = true;

		@Config.LangKey("randomtweaks.config.timeOfDay.enabledByDefault")
		@Config.Comment("Enables the overlay by default.")
		public boolean enabledByDefault = Utils.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.timeOfDay.disableIfNoDaylightCycle")
		@Config.Comment("Disables the overlay if doDaylightCycle is false.")
		public boolean disableIfNoDaylightCycle = !Utils.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.timeOfDay.disableInAdventureMode")
		@Config.Comment("Disables the overlay in Adventure Mode.")
		public boolean disableInAdventureMode = true;

		@Config.LangKey("randomtweaks.config.timeOfDay.twentyFourHourTime")
		@Config.Comment("Enables 24-hour time.")
		public boolean twentyFourHourTime = Utils.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.timeOfDay.alignment")
		@Config.Comment("The alignment.")
		public Alignment alignment = Alignment.TOPLEFT;

		@Config.LangKey("randomtweaks.config.timeOfDay.x")
		@Config.Comment("The X offset.")
		public int x = 0;

		@Config.LangKey("randomtweaks.config.timeOfDay.y")
		@Config.Comment("The Y offset.")
		public int y = 0;
	}

	@Config.LangKey("randomtweaks.config.client")
	@Config.Comment("Options related to client-sided features.")
	public static Client client = new Client();

	@Config.LangKey("randomtweaks.config.commands")
	@Config.Comment("Options related to commands.")
	public static Commands commands = new Commands();

	@Config.LangKey("randomtweaks.config.general")
	@Config.Comment("Options that don't fit into any other categories.")
	public static General general = new General();

	@Config.LangKey("randomtweaks.config.hunger")
	@Config.Comment("Options related to hunger behavior.")
	public static Hunger hunger = new Hunger();

	@Config.LangKey("randomtweaks.config.playerHeadDrops")
	@Config.Comment("Options related to player head drops.")
	public static PlayerHeadDrops playerHeadDrops = new PlayerHeadDrops();

	@Config.LangKey("randomtweaks.config.world")
	@Config.Comment("Options related to world generation.")
	public static World world = new World();

	@Config.LangKey("randomtweaks.config.squids")
	@Config.Comment("Options related to squid spawning behavior.")
	public static Squids squids = new Squids();

	@Config.Ignore
	public static AutoThirdPerson autoThirdPerson = client.autoThirdPerson;
	@Config.Ignore
	public static Ding ding = client.ding;
	@Config.Ignore
	public static Keybinds keybinds = client.keybinds;
	@Config.Ignore
	public static TimeOfDay timeOfDay = client.timeOfDay;
	@Config.Ignore
	public static OceanFloor oceanFloor = world.oceanFloor;

	public static class DefaultGamerules {
		public static final List<String> DEFAULT = Arrays.asList(
				"//Example configuration." +
						"The game does not need to be restarted after changing this.",
				"{",
				"//\t\"commandBlockOutput\": false, //These are for all game modes.",
				"//\t\"keepInventory\": true,",
				"//\t\"1:flat,void\": { //Affects creative flat and void worlds. " +
						"Game modes and world types can be separated with commas without spaces. " +
						"These are the same as the world types in server.properties.",
				"//\t\t\"doDaylightCycle\": false,",
				"//\t\t\"doWeatherCycle\": false,",
				"//\t\t\"doMobSpawning\": false,",
				"//\t\t\"rtWorldBorderSize\": 10000 //This is not actually a gamerule, but is " +
						"used to set the world border in blocks from the chunk (0, 0).",
				"//\t}",
				"}"
		);

		public static void create() throws IOException {
			final Path path = Paths.get("config", RandomTweaks.MODID, "defaultgamerules.json");

			if(Files.exists(path)) {
				Files.move(path, Paths.get(path.toString() + "_old" + System.nanoTime()));
			}

			Files.write(path, DEFAULT);
		}

		public static void ensureExists() throws IOException {
			if(!Files.exists(getJson("defaultgamerules"))) {
				create();
			}
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

		//Format: comma-separated integer gamemodes:comma-separated world types
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
		try {
			ConfigManager.sync(RandomTweaks.MODID, Config.Type.INSTANCE);
			DefaultGamerules.ensureExists();
			Data.data = null;
		} catch(IOException ex) {
			Utils.crashReport("Failed to reload config", ex);
		}
	}
}
