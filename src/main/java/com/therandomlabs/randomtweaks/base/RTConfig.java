package com.therandomlabs.randomtweaks.base;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.therandomlabs.randomtweaks.common.RespawnHandler;
import com.therandomlabs.randomtweaks.common.TrampleHandler;
import com.therandomlabs.randomtweaks.util.Alignment;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.commons.lang3.StringUtils;

@Mod.EventBusSubscriber(modid = RandomTweaks.MODID)
@Config(modid = RandomTweaks.MODID, name = RTConfig.NAME, category = "")
public class RTConfig {
	@Config.Ignore
	public static final String NAME = RandomTweaks.MODID + "/" + RandomTweaks.MODID;

	public static class Animals {
		@Config.LangKey("randomtweaks.config.randomizedAges")
		@Config.Comment("Options related to randomized animal ages.")
		public RandomizedAges randomizedAges = new RandomizedAges();

		@Config.LangKey("randomtweaks.config.squids")
		@Config.Comment("Options related to squid spawning behavior.")
		public Squids squids = new Squids();

		@Config.LangKey("randomtweaks.config.animals.coloredSheep")
		@Config.Comment("Whether colored sheep should spawn.")
		public boolean coloredSheep = true;

		@Config.LangKey("randomtweaks.config.animals.leashableVillagers")
		@Config.Comment("Allows villagers to be leashed.")
		public boolean leashableVillagers = true;

		@Config.LangKey("randomtweaks.config.animals.ocelotsCanBeHealed")
		@Config.Comment("Tamed Ocelots can be healed with fish.")
		public boolean ocelotsCanBeHealed = true;

		@Config.LangKey("randomtweaks.config.animals.parrotsCanBeHealed")
		@Config.Comment("Tamed Parrots can be healed with seeds.")
		public boolean parrotsCanBeHealed = true;

		@Config.LangKey("randomtweaks.config.animals.protectPetsFromOtherPets")
		@Config.Comment("Prevents pets from being attacked by their owner's other pets.")
		public boolean protectPetsFromOtherPets = true;

		@Config.LangKey("randomtweaks.config.animals.protectPetsFromOwners")
		@Config.Comment("Prevents pets from being attacked by their owners " +
				"(unless they're sneaking).")
		public boolean protectPetsFromOwners = true;
	}

	public static class AutoThirdPerson {
		@Config.LangKey("entity.Boat.name")
		@Config.Comment("Whether auto-third person is enabled when riding a boat.")
		public boolean boat = Constants.IS_DEOBFUSCATED;

		@Config.LangKey("item.elytra.name")
		@Config.Comment("Whether auto-third person is enabled when flying with elytra.")
		public boolean elytra = true;

		@Config.LangKey("randomtweaks.config.autoThirdPerson.enabled")
		@Config.Comment("Whether auto-third person is enabled.")
		public boolean enabled = true;

		@Config.LangKey("entity.Horse.name")
		@Config.Comment("Whether auto-third person is enabled when riding a horse.")
		public boolean horse = Constants.IS_DEOBFUSCATED;

		@Config.LangKey("item.minecart.name")
		@Config.Comment("Whether auto-third person is enabled when riding a minecart.")
		public boolean minecart = Constants.IS_DEOBFUSCATED;

		@Config.LangKey("entity.Pig.name")
		@Config.Comment("Whether auto-third person is enabled when riding a pig.")
		public boolean pig = Constants.IS_DEOBFUSCATED;
	}

	public static class BoneMeal {
		@Config.RangeInt(min = 0, max = 16)
		@Config.LangKey("randomtweaks.config.boneMeal.cacti")
		@Config.Comment("The amount of stages Bone Meal should cause Cacti to grow. " +
				"16 is a full block.")
		public int cacti = 8;

		@Config.LangKey("randomtweaks.config.boneMeal.netherWarts")
		@Config.Comment("Whether Bone Meal should grow Nether Warts.")
		public boolean netherWarts = true;

		@Config.RangeInt(min = 0, max = 16)
		@Config.LangKey("randomtweaks.config.boneMeal.sugarCanes")
		@Config.Comment("The amount of stages Bone Meal should cause Sugar Canes to grow. " +
				"16 is a full block.")
		public int sugarCanes = 8;
	}

	public static class Client {
		@Config.LangKey("randomtweaks.config.autoThirdPerson")
		@Config.Comment("Options related to the auto-third person feature.")
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

		@Config.LangKey("randomtweaks.config.client.clearWater")
		@Config.Comment("Removes underwater fog.")
		public boolean clearWater = true;

		@Config.RequiresWorldRestart
		@Config.LangKey("randomtweaks.config.client.contributorCapes")
		@Config.Comment("Enables contributor capes.")
		public boolean contributorCapes = true;

		@Config.LangKey("randomtweaks.config.client.disableEnderDragonDeathSound")
		@Config.Comment("Disables the Ender Dragon death sound.")
		public boolean disableEnderDragonDeathSound;

		@Config.LangKey("randomtweaks.config.client.disablePotionIcons")
		@Config.Comment("Whether to disable the potion icons at the top right.")
		public boolean disablePotionIcons = Constants.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.client.disablePotionShift")
		@Config.Comment("Stops potion effects from moving GUIs to the right.")
		public boolean disablePotionShift = true;

		@Config.LangKey("randomtweaks.config.client.disableWitherSpawnSound")
		@Config.Comment("Disables the Wither spawn sound.")
		public boolean disableWitherSpawnSound;

		@Config.LangKey("randomtweaks.config.client.moveBucketCreativeTab")
		@Config.Comment("Moves the Bucket to the Tools creative tab.")
		public boolean moveBucketCreativeTab = true;

		@Config.LangKey("randomtweaks.config.client.spawnEggsCreativeTab")
		@Config.Comment("Moves spawn eggs to their own creative tab.")
		public boolean spawnEggsCreativeTab = true;

		@Config.LangKey("randomtweaks.config.client.stepup")
		@Config.Comment("Whether to enable stepup.")
		public boolean stepup = Constants.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.client.stepupEnabledByDefault")
		@Config.Comment("Whether stepup is enabled by default.")
		public boolean stepupEnabledByDefault;
	}

	public static class Commands {
		@Config.RequiresWorldRestart
		@Config.LangKey("randomtweaks.config.commands.deletegamerule")
		@Config.Comment("Enables the /deletegamerule command.")
		public boolean deletegamerule = true;

		@Config.RequiresMcRestart
		@Config.LangKey("randomtweaks.config.commands.disconnect")
		@Config.Comment("Enables the client-sided /disconnect command, which leaves the " +
				"current world.")
		public boolean disconnect = true;

		@Config.RequiresWorldRestart
		@Config.LangKey("randomtweaks.config.commands.giveTweaks")
		@Config.Comment("Allows /give to accept integer IDs, amounts higher than 64 and " +
				"ore dictionary names prefixed by \"ore:\".")
		public boolean giveTweaks = true;

		@Config.RequiresWorldRestart
		@Config.LangKey("randomtweaks.config.commands.helpTweaks")
		@Config.Comment("Changes the /help command to make sure that sorting commands " +
				"doesn't cause an error.")
		public boolean helpTweaks = true;

		@Config.RequiresWorldRestart
		@Config.LangKey("randomtweaks.config.commands.hunger")
		@Config.Comment("Enables the /hunger command, which sets a player's hunger level.")
		public boolean hunger = true;

		@Config.RequiresWorldRestart
		@Config.LangKey("randomtweaks.config.commands.rtreload")
		@Config.Comment("Enables the /rtreload command.")
		public boolean rtreload = true;

		@Config.RequiresMcRestart
		@Config.LangKey("randomtweaks.config.commands.rtreloadclient")
		@Config.Comment("Enables the client-sided /rtreloadclient command.")
		public boolean rtreloadclient = true;
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

	public static class Hunger {
		@Config.LangKey("randomtweaks.config.hunger.carryExcessHungerToSaturation")
		@Config.Comment("If this is enabled, any excess hunger level gained by eating will be " +
				"added to the saturation.")
		public boolean carryExcessHungerToSaturation = Constants.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.hunger.halveExhaustion")
		@Config.Comment("Halves exhaustion values.")
		public boolean halveExhaustion = Constants.IS_DEOBFUSCATED;

		@Config.RangeInt(min = 1)
		@Config.LangKey("randomtweaks.config.hunger.maximumHungerLevel")
		@Config.Comment("The maximum hunger level.")
		public int maximumHungerLevel = Constants.IS_DEOBFUSCATED ? 22 : 20;

		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.hunger.minimumRespawnHungerLevel")
		@Config.Comment("The minimum hunger level on respawn.")
		public int minimumRespawnHungerLevel = 3;

		@Config.LangKey("randomtweaks.config.hunger.respawnResetBehavior")
		@Config.Comment("What happens to a player's hunger when they respawn. " +
				"This has no effect in creative mode.")
		public RespawnHandler.HungerResetBehavior respawnResetBehavior =
				RespawnHandler.HungerResetBehavior.RESET_UNLESS_KEEP_INVENTORY;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("randomtweaks.config.hunger.saturationLimit")
		@Config.Comment("This value is added to the player's food level to calculate the maximum " +
				"saturation level.")
		public double saturationLimit = Constants.IS_DEOBFUSCATED ? 100.0 : 0.0;
	}

	public static class Keybinds {
		@Config.LangKey("randomtweaks.config.keybinds.noclip")
		@Config.Comment("Enables the Noclip keybind, which toggles between /gamemode c " +
				"and /gamemode sp.")
		public boolean noclip = true;

		@Config.LangKey("randomtweaks.config.keybinds.fovChangesEnabledByDefault")
		@Config.Comment("Whether FoV changes are enabled by default.")
		public boolean fovChangesEnabledByDefault = true;

		@Config.LangKey("randomtweaks.config.keybinds.fovChangesStatusMessage")
		@Config.Comment("Whether a status message should be displayed when FoV changes are " +
				"toggled.")
		public boolean fovChangesStatusMessage = true;

		@Config.LangKey("randomtweaks.config.keybinds.reloadSoundSystem")
		@Config.Comment("Enables the Reload Sound System keybind.")
		public boolean reloadSoundSystem = true;

		@Config.LangKey("randomtweaks.config.keybinds.toggleFoVChanges")
		@Config.Comment("Enables the Toggle FoV Changes keybind.")
		public boolean toggleFoVChanges = true;

		@Config.LangKey("randomtweaks.config.keybinds.toggleTimeOfDayOverlay")
		@Config.Comment("Enables the Toggle Time of Day Overlay keybind.")
		public boolean toggleTimeOfDayOverlay = true;
	}

	public static class Misc {
		@Config.RangeDouble(min = 0.0, max = 1024.0)
		@Config.LangKey("randomtweaks.config.misc.attackSpeed")
		@Config.Comment("Sets the base attack speed for players. Set this to 16.0 or higher " +
				"to remove the attack cooldown. 4.0 is the default value.")
		public double attackSpeed = Constants.IS_DEOBFUSCATED ? 24.0 : 4.0;

		@Config.LangKey("randomtweaks.config.misc.deathPunishmentMode")
		@Config.Comment("Sets when death punishments are enabled in which all XP and 3 hunger " +
				"points are removed. This is always disabled in creative mode and overrides " +
				"hunger.respawnResetBehavior.")
		public RespawnHandler.DeathPunishmentMode deathPunishmentMode =
				RespawnHandler.DeathPunishmentMode.ENABLED_IF_KEEP_INVENTORY;

		@Config.LangKey("randomtweaks.config.misc.disableCumulativeAnvilCost")
		@Config.Comment("Disables the cumulative anvil cost.")
		public boolean disableCumulativeAnvilCost = true;

		@Config.LangKey("randomtweaks.config.misc.disableNetherPortalCreationGamerule")
		@Config.Comment("The name of the gamerule that disables Nether portal creation. " +
				"Set this to an empty string to disable the gamerule.")
		public String disableNetherPortalCreationGamerule = "disableNetherPortalCreation";

		@Config.LangKey("randomtweaks.config.misc.farmlandTrampleBehavior")
		@Config.Comment("The farmland trample behavior.")
		public TrampleHandler.Behavior farmlandTrampleBehavior =
				TrampleHandler.Behavior.DONT_TRAMPLE_IF_FEATHER_FALLING;

		@Config.LangKey("randomtweaks.config.misc.moreRomanNumerals")
		@Config.Comment("Enables Roman numerals from -32768 to 32767.")
		public boolean moreRomanNumerals = true;

		@Config.LangKey("randomtweaks.config.misc.pickUpSkeletonArrows")
		@Config.Comment("Allows skeleton arrows to be picked up.")
		public boolean pickUpSkeletonArrows = Constants.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.misc.requireFullCubeForSpawns")
		@Config.Comment("Disables mob spawning on non-full cubes.")
		public boolean requireFullCubeForSpawns = Constants.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.misc.sleepTweaks")
		@Config.Comment("Allows players to sleep near mobs with custom names.")
		public boolean sleepTweaks = true;
	}

	public static class OceanFloor {
		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.oceanFloor.clayChance")
		@Config.Comment("The clay spawn chance.")
		public int clayChance = 65;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.oceanFloor.clayVeinSize")
		@Config.Comment("The amount of clay in a vein.")
		public int clayVeinSize = 32;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.oceanFloor.dirtChance")
		@Config.Comment("The dirt spawn chance.")
		public int dirtChance = 30;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.oceanFloor.dirtVeinSize")
		@Config.Comment("The amount of dirt in a vein.")
		public int dirtVeinSize = 18;

		@Config.RequiresMcRestart
		@Config.LangKey("randomtweaks.config.oceanFloor.enabled")
		@Config.Comment("Enables the ocean floor worldgen.")
		public boolean enabled = true;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.oceanFloor.maxY")
		@Config.Comment("The maximum Y coordinate.")
		public int maxY = 128;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.oceanFloor.minY")
		@Config.Comment("The minimum Y coordinate.")
		public int minY = 20;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.oceanFloor.sandChance")
		@Config.Comment("The sand spawn chance.")
		public int sandChance = 45;

		@Config.RequiresMcRestart
		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.oceanFloor.sandVeinSize")
		@Config.Comment("The amount of sand in a vein.")
		public int sandVeinSize = 22;
	}

	public static class PlayerHeadDrops {
		@Config.RangeDouble(min = 0.0, max = 1.0)
		@Config.LangKey("randomtweaks.config.playerHeadDrops.chanceWhenKilledByChargedCreeper")
		@Config.Comment("The player head drop chance when a player is killed by a charged creeper.")
		public double chanceWhenKilledByChargedCreeper = 1.0;

		@Config.RangeDouble(min = 0.0, max = 1.0)
		@Config.LangKey("randomtweaks.config.playerHeadDrops.chanceWhenKilledByPlayer")
		@Config.Comment("The player head drop chance when a player is killed by another player.")
		public double chanceWhenKilledByPlayer = 1.0;

		@Config.LangKey("randomtweaks.config.playerHeadDrops.enabled")
		@Config.Comment("Whether players should drop their heads when they die.")
		public boolean enabled = true;

		@Config.RangeDouble(min = 0.0, max = 1.0)
		@Config.LangKey("randomtweaks.config.playerHeadDrops.normalChance")
		@Config.Comment("The normal player head drop chance.")
		public double normalChance = 1.0;
	}

	public static class RandomizedAges {
		@Config.RangeDouble(min = 0.0, max = 1.0)
		@Config.LangKey("randomtweaks.config.randomizedAges.chance")
		@Config.Comment("The chance that an animal's age is randomized.")
		public double chance = 0.1;

		@Config.LangKey("randomtweaks.config.randomizedAges.maximumAge")
		@Config.Comment("The maximum age in ticks.")
		public int maximumAge = -6000;

		@Config.LangKey("randomtweaks.config.randomizedAges.minimumAge")
		@Config.Comment("The minimum age in ticks.")
		public int minimumAge = -24000;
	}

	public static class Squids {
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

		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.squids.spawnRadiusLimit")
		@Config.Comment("Disables squid spawning when a player is not within the specified " +
				"radius. Set this to 0 to disable this limit.")
		public int spawnRadiusLimit = 64;
	}

	public static class TimeOfDay {
		@Config.LangKey("randomtweaks.config.timeOfDay.alignment")
		@Config.Comment("The alignment.")
		public Alignment alignment = Alignment.TOPLEFT;

		@Config.LangKey("randomtweaks.config.timeOfDay.disableIfNoDaylightCycle")
		@Config.Comment("Disables the overlay if doDaylightCycle is false.")
		public boolean disableIfNoDaylightCycle = !Constants.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.timeOfDay.disableInAdventureMode")
		@Config.Comment("Disables the overlay in Adventure Mode.")
		public boolean disableInAdventureMode = true;

		@Config.LangKey("randomtweaks.config.timeOfDay.disableInGUIs")
		@Config.Comment("Disables the overlay in GUIs.")
		public boolean disableInGUIs = true;

		@Config.LangKey("randomtweaks.config.timeOfDay.enabled")
		@Config.Comment("Enables the overlay.")
		public boolean enabled = true;

		@Config.LangKey("randomtweaks.config.timeOfDay.enabledByDefault")
		@Config.Comment("Enables the overlay by default.")
		public boolean enabledByDefault = Constants.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.timeOfDay.twentyFourHourTime")
		@Config.Comment("Enables 24-hour time.")
		public boolean twentyFourHourTime = Constants.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.timeOfDay.x")
		@Config.Comment("The X offset.")
		public int x = 0;

		@Config.LangKey("randomtweaks.config.timeOfDay.y")
		@Config.Comment("The Y offset.")
		public int y = 0;
	}

	public static class World {
		@Config.LangKey("randomtweaks.config.oceanFloor")
		@Config.Comment("Options related to ocean floor generation.")
		public OceanFloor oceanFloor = new OceanFloor();

		@Config.LangKey("randomtweaks.config.world.realisticWorldType")
		@Config.Comment("Enables the Realistic world type. Name: REALISTIC")
		public boolean realisticWorldType = true;

		@Config.RangeInt(min = 2)
		@Config.LangKey("randomtweaks.config.world.voidIslandsChunkRarity")
		@Config.Comment("The rarity of non-empty chunks in a Void Islands world. " +
				"If n, there is a 1 in n chance of a chunk being non-empty.")
		public int voidIslandsChunkRarity = 10;

		@Config.LangKey("randomtweaks.config.world.voidIslandsWorldBiome")
		@Config.Comment("The biome of the void chunks of a Void Islands world. " +
				"Leave this empty to randomize the biomes.")
		public String voidIslandsWorldBiome = "minecraft:plains";

		@Config.LangKey("randomtweaks.config.world.voidIslandsWorldType")
		@Config.Comment("Enables the Void Islands world type. Name: VOIDISLANDS")
		public boolean voidIslandsWorldType = true;

		@Config.LangKey("randomtweaks.config.world.voidWorldBiome")
		@Config.Comment("The biome of a Void world. Leave this empty to randomize the biomes.")
		public String voidWorldBiome = "minecraft:plains";

		@Config.LangKey("randomtweaks.config.world.voidWorldBlock")
		@Config.Comment("The block placed in a Void world for players to stand on.")
		public String voidWorldBlock = "minecraft:glass";

		@Config.LangKey("randomtweaks.config.world.voidWorldType")
		@Config.Comment("Enables the Void world type. Name: VOID")
		public boolean voidWorldType = true;

		@Config.RangeInt(min = 1, max = 255)
		@Config.LangKey("randomtweaks.config.world.voidWorldYSpawn")
		@Config.Comment("The Y coordinate of the default spawn point in a Void world.")
		public int voidWorldYSpawn = 17;
	}

	@Config.LangKey("randomtweaks.config.animals")
	@Config.Comment("Options related to animals (including villagers).")
	public static Animals animals = new Animals();

	@Config.LangKey("randomtweaks.config.boneMeal")
	@Config.Comment("Options related to bone meal.")
	public static BoneMeal boneMeal = new BoneMeal();

	@Config.LangKey("randomtweaks.config.client")
	@Config.Comment("Options related to features that only work client-side.")
	public static Client client = new Client();

	@Config.LangKey("randomtweaks.config.commands")
	@Config.Comment("Options related to commands.")
	public static Commands commands = new Commands();

	@Config.LangKey("randomtweaks.config.hunger")
	@Config.Comment("Options related to hunger behavior.")
	public static Hunger hunger = new Hunger();

	@Config.LangKey("randomtweaks.config.misc")
	@Config.Comment("Options that don't fit into any other categories.")
	public static Misc misc = new Misc();

	@Config.LangKey("randomtweaks.config.playerHeadDrops")
	@Config.Comment("Options related to player head drops.")
	public static PlayerHeadDrops playerHeadDrops = new PlayerHeadDrops();

	@Config.LangKey("randomtweaks.config.world")
	@Config.Comment("Options related to world generation.")
	public static World world = new World();

	@Config.Ignore
	public static AutoThirdPerson autoThirdPerson = client.autoThirdPerson;
	@Config.Ignore
	public static Ding ding = client.ding;
	@Config.Ignore
	public static Keybinds keybinds = client.keybinds;
	@Config.Ignore
	public static RandomizedAges randomizedAges = animals.randomizedAges;
	@Config.Ignore
	public static Squids squids = animals.squids;
	@Config.Ignore
	public static TimeOfDay timeOfDay = client.timeOfDay;
	@Config.Ignore
	public static OceanFloor oceanFloor = world.oceanFloor;

	public static class SheepColorWeights {
		public static final Path PATH = getJson("sheepcolorweights");
		public static final Map<EnumDyeColor, Double> WEIGHTS = new LinkedHashMap<>();

		public static void create() {
			try {
				final JsonObject json = new JsonObject();

				json.addProperty(EnumDyeColor.WHITE.getName(), 70.0);

				for(EnumDyeColor color : EnumDyeColor.values()) {
					if(color != EnumDyeColor.WHITE) {
						json.addProperty(color.getName(), 2.0);
					}
				}

				final String string = new GsonBuilder().setPrettyPrinting().create().toJson(json);
				Files.write(PATH, Collections.singletonList(string.replaceAll(" {2}", "\t")));
			} catch(IOException ex) {
				Utils.crashReport("Failed to create: " + PATH, ex);
			}
		}

		public static void ensureExists() {
			if(!Files.exists(PATH)) {
				create();
			}

			get();
		}

		public static void get() {
			if(!Files.exists(PATH)) {
				create();
				get();
				return;
			}

			JsonObject object;

			try {
				object = readJson(PATH);
			} catch(JsonSyntaxException ex) {
				RandomTweaks.LOGGER.error("Error in the RandomTweaks sheep color weights JSON. " +
						"The file will be replaced.", ex);

				create();
				get();

				return;
			}


			final EnumDyeColor[] colors = EnumDyeColor.values();
			final Map<String, EnumDyeColor> names = new HashMap<>(colors.length);

			for(EnumDyeColor color : colors) {
				names.put(color.getName(), color);
			}

			WEIGHTS.clear();

			try {
				for(Map.Entry<String, JsonElement> entry : object.entrySet()) {
					final String name = entry.getKey();

					final EnumDyeColor color = names.get(name);

					if(color != null) {
						WEIGHTS.put(color, Double.parseDouble(entry.getValue().getAsString()));
					}
				}
			} catch(NumberFormatException ex) {
				RandomTweaks.LOGGER.error("Error in the RandomTweaks sheep color weights JSON. " +
						"The file will be replaced.", ex);

				create();
				get();
			}
		}
	}

	public static class Data {
		public static final Path PATH = getJson("data");

		private static Data data;

		public Map<String, Boolean> timeOfDayOverlay;
		public boolean stepup;
		public boolean fovChanges;

		public static Data get() {
			if(data != null) {
				return data;
			}

			if(Files.exists(PATH)) {
				try {
					data = new Gson().fromJson(readFile(PATH), Data.class);
				} catch(JsonSyntaxException ex) {
					RandomTweaks.LOGGER.error("Error in the RandomTweaks data JSON. " +
							"The file will be replaced.", ex);
				}
			}

			if(data == null) {
				data = new Data();

				data.timeOfDayOverlay = new HashMap<>();
				data.stepup = client.stepupEnabledByDefault;
				data.fovChanges = keybinds.fovChangesEnabledByDefault;

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
				Files.write(PATH, Collections.singletonList(new Gson().toJson(data)));
			} catch(IOException ex) {
				Utils.crashReport("Error while saving RandomTweaks data", ex);
			}
		}
	}

	private static final Method GET_CONFIGURATION = ReflectionHelper.findMethod(ConfigManager.class,
			"getConfiguration", "getConfiguration", String.class, String.class);

	public static Path getConfig(String name) {
		final Path path = Paths.get("config", RandomTweaks.MODID, name);
		final Path parent = path.getParent();

		try {
			if(parent != null) {
				if(Files.exists(parent) && !Files.isDirectory(parent)) {
					Files.delete(parent);
				}

				Files.createDirectories(parent);
			}
		} catch(IOException ex) {
			Utils.crashReport("Failed to create parent: " + path, ex);
		}

		return path;
	}

	public static Path getJson(String name) {
		return getConfig(name + ".json");
	}

	public static String readFile(Path path) {
		try {
			return StringUtils.join(Files.readAllLines(path), System.lineSeparator());
		} catch(IOException ex) {
			Utils.crashReport("Failed to read file: " + path, ex);
		}

		return null;
	}

	public static JsonObject readJson(Path path) {
		return new JsonParser().parse(readFile(path)).getAsJsonObject();
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(event.getModID().equals(RandomTweaks.MODID)) {
			reload();
		}
	}

	public static void reload() {
		ConfigManager.sync(RandomTweaks.MODID, Config.Type.INSTANCE);

		try {
			modifyConfig();
			ConfigManager.sync(RandomTweaks.MODID, Config.Type.INSTANCE);
			modifyConfig();
		} catch(Exception ex) {
			Utils.crashReport("Error while modifying config", ex);
		}

		if(animals.coloredSheep) {
			SheepColorWeights.ensureExists();
		}

		Data.data = null;
	}

	private static void modifyConfig() throws Exception {
		final Configuration config =
				(Configuration) GET_CONFIGURATION.invoke(null, RandomTweaks.MODID, NAME);

		final Map<Property, String> comments = new HashMap<>();

		//Remove old elements
		for(String name : config.getCategoryNames()) {
			final ConfigCategory category = config.getCategory(name);

			category.getValues().forEach((key, property) -> {
				final String comment = property.getComment();

				if(comment == null || comment.isEmpty()) {
					category.remove(key);
					return;
				}

				if("maximumAge".equals(key) &&
						randomizedAges.maximumAge < randomizedAges.minimumAge) {
					property.set(randomizedAges.minimumAge);
					randomizedAges.maximumAge = randomizedAges.minimumAge;
				}

				//Add default value to comment
				comments.put(property, comment);
				property.setComment(comment + "\n" + "Default: " + property.getDefault());
			});

			if(category.getValues().isEmpty() || category.getComment() == null) {
				config.removeCategory(category);
			}
		}

		config.save();

		//Remove default values from comments so they don't show up in the configuration GUI
		for(String name : config.getCategoryNames()) {
			config.getCategory(name).getValues().forEach((key, property) ->
					property.setComment(comments.get(property)));
		}
	}
}
