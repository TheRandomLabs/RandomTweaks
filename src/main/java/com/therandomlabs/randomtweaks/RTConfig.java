package com.therandomlabs.randomtweaks;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.therandomlabs.randomtweaks.common.RespawnHandler;
import com.therandomlabs.randomtweaks.common.SquidHandler;
import com.therandomlabs.randomtweaks.common.TrampleHandler;
import com.therandomlabs.randomtweaks.common.world.ChunkGeneratorVoidIslands;
import com.therandomlabs.randomtweaks.util.Alignment;
import com.therandomlabs.randomtweaks.util.RTUtils;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

@Mod.EventBusSubscriber(modid = RandomTweaks.MOD_ID)
@Config(modid = RandomTweaks.MOD_ID, name = RTConfig.NAME, category = "")
public final class RTConfig {
	public static final class Data {
		private static Data data;

		public Map<String, Boolean> timeOfDayOverlay;
		public boolean stepup;
		public boolean fovChanges;

		public static Data get() {
			if(data != null) {
				return data;
			}

			final Path path = getPath();

			if(path.toFile().exists()) {
				try {
					data = new Gson().fromJson(readFile(path), Data.class);
				} catch(JsonSyntaxException ex) {
					RandomTweaks.LOGGER.error(
							"Error in the RandomTweaks data JSON. The file will be replaced.", ex
					);
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
				Files.write(getPath(), Collections.singletonList(new Gson().toJson(data)));
			} catch(IOException ex) {
				RTUtils.crashReport("Error while saving RandomTweaks data", ex);
			}
		}

		public static Path getPath() {
			return client.storeDataInLocal ? getJson("../../local/client/rtdata") : getJson("data");
		}
	}

	public static final class Animals {
		@Config.LangKey("randomtweaks.config.randomizedAges")
		@Config.Comment("Options related to randomized animal ages.")
		public final RandomizedAges randomizedAges = new RandomizedAges();

		@Config.LangKey("randomtweaks.config.sheepColorWeights")
		@Config.Comment("Sheep color weights.")
		public final SheepColorWeights sheepColorWeights = new SheepColorWeights();

		@Config.LangKey("randomtweaks.config.squids")
		@Config.Comment("Options related to squid spawning behavior.")
		public final Squids squids = new Squids();

		@Config.RangeDouble(min = 0.0, max = 1.0)
		@Config.LangKey("randomtweaks.config.animals.batLeatherDropChance")
		@Config.Comment("The chance that a bat drops a piece of leather when killed.")
		public double batLeatherDropChance = 0.8;

		@Config.LangKey("randomtweaks.config.animals.leashableVillagers")
		@Config.Comment("Whether villagers should be leashable.")
		public boolean leashableVillagers = true;

		@Config.LangKey("randomtweaks.config.animals.ocelotsCanBeHealed")
		@Config.Comment("Whether tamed ocelots can be healed with fish.")
		public boolean ocelotsCanBeHealed = true;

		@Config.LangKey("randomtweaks.config.animals.parrotsCanBeHealed")
		@Config.Comment("Whether tamed parrots can be healed with seeds.")
		public boolean parrotsCanBeHealed = true;

		@Config.LangKey("randomtweaks.config.animals.protectPetsFromOtherPets")
		@Config.Comment("Prevents pets from being attacked by their owner's other pets.")
		public boolean protectPetsFromOtherPets = true;

		@Config.LangKey("randomtweaks.config.animals.protectPetsFromOwners")
		@Config.Comment({
				"Prevents pets from being attacked by their owners.",
				"By default, owners can still attack their pets while sneaking."
		})
		public boolean protectPetsFromOwners = true;

		@Config.LangKey("randomtweaks.config.animals.protectPetsFromSneakingOwners")
		@Config.Comment("Prevents pets from being attacked by their owners while sneaking.")
		public boolean protectPetsFromSneakingOwners;
	}

	public static class AutoThirdPerson {
		@Config.LangKey("entity.Boat.name")
		@Config.Comment("Enables auto-third person upon entering a boat.")
		public boolean boat = RandomTweaks.IS_DEOBFUSCATED;

		@Config.LangKey("item.elytra.name")
		@Config.Comment("Enables auto-third person upon flying with elytra.")
		public boolean elytra = true;

		@Config.LangKey("randomtweaks.config.autoThirdPerson.enabled")
		@Config.Comment("Enables auto-third person.")
		public boolean enabled = true;

		@Config.LangKey("entity.Horse.name")
		@Config.Comment("Enables auto-third person upon mounting a horse.")
		public boolean horse = RandomTweaks.IS_DEOBFUSCATED;

		@Config.LangKey("item.minecart.name")
		@Config.Comment("Enables auto-third person upon entering a minecart.")
		public boolean minecart = RandomTweaks.IS_DEOBFUSCATED;

		@Config.LangKey("entity.Pig.name")
		@Config.Comment("Enables auto-third person upon mounting a pig.")
		public boolean pig = RandomTweaks.IS_DEOBFUSCATED;
	}

	public static class BoneMeal {
		@Config.RangeInt(min = 0, max = 16)
		@Config.LangKey("randomtweaks.config.boneMeal.cacti")
		@Config.Comment({
				"The amount of stages bone meal should cause cacti to grow.",
				"16 stages is a full block."
		})
		public int cacti = 8;

		@Config.LangKey("randomtweaks.config.boneMeal.netherWart")
		@Config.Comment("Whether bone meal should grow nether wart.")
		public boolean netherWart = true;

		@Config.RangeInt(min = 0, max = 16)
		@Config.LangKey("randomtweaks.config.boneMeal.sugarCanes")
		@Config.Comment({
				"The amount of stages bone meal should cause sugar canes to grow.",
				"16 stages is a full block."
		})
		public int sugarCanes = 8;
	}

	public static class Client {
		@Config.LangKey("randomtweaks.config.autoThirdPerson")
		@Config.Comment("Options related to the auto-third person feature.")
		public final AutoThirdPerson autoThirdPerson = new AutoThirdPerson();

		@Config.LangKey("randomtweaks.config.creativeTabs")
		@Config.Comment("Options related to creative tabs.")
		public final CreativeTabs creativeTabs = new CreativeTabs();

		@Config.LangKey("randomtweaks.config.ding")
		@Config.Comment("Options related to the 'ding' feature.")
		public final Ding ding = new Ding();

		@Config.LangKey("randomtweaks.config.keybinds")
		@Config.Comment("Options related to keybinds.")
		public final Keybinds keybinds = new Keybinds();

		@Config.LangKey("randomtweaks.config.timeOfDay")
		@Config.Comment("Options related to the time of day overlay.")
		public final TimeOfDay timeOfDay = new TimeOfDay();

		@Config.LangKey("randomtweaks.config.client.clearWater")
		@Config.Comment("Removes underwater fog.")
		public boolean clearWater = true;

		@Config.RequiresWorldRestart
		@Config.LangKey("randomtweaks.config.client.contributorCapes")
		@Config.Comment("Whether contributor capes should be enabled.")
		public boolean contributorCapes = true;

		@Config.LangKey("randomtweaks.config.client.disableEnderDragonDeathSound")
		@Config.Comment("Disables the ender dragon death sound.")
		public boolean disableEnderDragonDeathSound;

		@Config.LangKey("randomtweaks.config.client.disablePotionIcons")
		@Config.Comment("Disables the potion icons at the top right.")
		public boolean disablePotionIcons = RandomTweaks.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.client.disablePotionShift")
		@Config.Comment("Stops potion effects from moving GUIs to the right.")
		public boolean disablePotionShift = true;

		@Config.LangKey("randomtweaks.config.client.disableWitherSpawnSound")
		@Config.Comment("Disables the wither spawn sound.")
		public boolean disableWitherSpawnSound;

		@Config.LangKey("randomtweaks.config.client.startOnMultiplayerScreen")
		@Config.Comment("Whether to start the game on the Multiplayer screen.")
		public boolean startOnMultiplayerScreen = RandomTweaks.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.client.stepup")
		@Config.Comment("Whether to enable stepup.")
		public boolean stepup = RandomTweaks.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.client.stepupEnabledByDefault")
		@Config.Comment("Whether stepup is enabled by default.")
		public boolean stepupEnabledByDefault = RandomTweaks.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.client.storeDataInLocal")
		@Config.Comment("Whether the toggle states for the time of day overlay, stepup and " +
				"FoV changes should be stored in local/client instead of config/randomtweaks.")
		public boolean storeDataInLocal = true;
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

	public static class CreativeTabs {
		@Config.LangKey("randomtweaks.config.creativeTabs.moveBucketCreativeTab")
		@Config.Comment("Moves the bucket to the Tools creative tab.")
		public boolean moveBucketCreativeTab = true;

		@Config.LangKey("randomtweaks.config.creativeTabs.noAISpawnEggs")
		@Config.Comment(
				"Adds spawn eggs to the Spawn Eggs creative tab that spawn entities without AIs."
		)
		public boolean noAISpawnEggs = true;

		@Config.LangKey("randomtweaks.config.creativeTabs.setCommandBlockCreativeTab")
		@Config.Comment("Adds the command blocks to the Redstone creative tab.")
		public boolean setCommandBlockCreativeTab = true;

		@Config.LangKey("randomtweaks.config.creativeTabs.setDragonEggCreativeTab")
		@Config.Comment("Adds the dragon egg to the Decorations creative tab.")
		public boolean setDragonEggCreativeTab = true;

		@Config.LangKey("randomtweaks.config.creativeTabs.spawnEggsCreativeTab")
		@Config.Comment("Moves spawn eggs to their own creative tab.")
		public boolean spawnEggsCreativeTab = true;
	}

	public static class Ding {
		@Config.LangKey("randomtweaks.config.ding.soundNames")
		@Config.Comment({
				"The names of the sounds to play when Minecraft starts.",
				"Leave this empty to disable this feature."
		})
		public String[] soundNames = new String[] {
				"entity.experience_orb.pickup"
		};

		@Config.RangeDouble(min = 0.0, max = 10.0)
		@Config.LangKey("randomtweaks.config.ding.soundPitch")
		@Config.Comment("The pitch of the sound to play when Minecraft starts.")
		public double soundPitch = 1.0;

		@Config.LangKey("randomtweaks.config.ding.worldSoundNames")
		@Config.Comment({
				"The names of the sounds to play when a world loads.",
				"Leave this empty to disable this feature."
		})
		public String[] worldSoundNames = new String[] {
				"entity.experience_orb.pickup"
		};

		@Config.RangeDouble(min = 0.0, max = 10.0)
		@Config.LangKey("randomtweaks.config.ding.worldSoundPitch")
		@Config.Comment("The pitch of the sound to play when a world loads.")
		public double worldSoundPitch = 1.0;
	}

	public static class Hunger {
		@Config.LangKey("randomtweaks.config.hunger.carryExcessHungerToSaturation")
		@Config.Comment("Carries any excess hunger level gained by eating over to the saturation.")
		public boolean carryExcessHungerToSaturation = RandomTweaks.IS_DEOBFUSCATED;

		@Config.RequiresMcRestart
		@Config.LangKey("randomtweaks.config.hunger.enabled")
		@Config.Comment("Enables hunger tweaks.")
		public boolean enabled = true;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("randomtweaks.config.hunger.exhaustionMultiplier")
		@Config.Comment("The hunger exhaustion multiplier.")
		public double exhaustionMultiplier = RandomTweaks.IS_DEOBFUSCATED ? 0.0 : 1.0;

		@Config.RangeInt(min = 1)
		@Config.LangKey("randomtweaks.config.hunger.maximumHungerLevel")
		@Config.Comment("The maximum hunger level.")
		public int maximumHungerLevel = RandomTweaks.IS_DEOBFUSCATED ? 22 : 20;

		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.hunger.minimumRespawnHungerLevel")
		@Config.Comment("The minimum hunger level on respawn.")
		public int minimumRespawnHungerLevel = 3;

		@Config.LangKey("randomtweaks.config.hunger.respawnResetBehavior")
		@Config.Comment({
				"What happens to a player's hunger when they respawn.",
				"This has no effect in creative mode."
		})
		public RespawnHandler.HungerResetBehavior respawnResetBehavior =
				RespawnHandler.HungerResetBehavior.RESET_UNLESS_KEEP_INVENTORY;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("randomtweaks.config.hunger.saturationLimit")
		@Config.Comment("The value added to the player's food level to calculate the maximum " +
				"saturation level.")
		public double saturationLimit = RandomTweaks.IS_DEOBFUSCATED ? 100.0 : 0.0;
	}

	public static class Keybinds {
		@Config.LangKey("randomtweaks.config.keybinds.fovChangesEnabledByDefault")
		@Config.Comment("Whether FoV changes should be enabled by default.")
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
		@Config.LangKey("randomtweaks.config.misc.allowSleepNearMobsWithCustomNames")
		@Config.Comment("Allows players to sleep near mobs with custom names.")
		public boolean allowSleepNearMobsWithCustomNames = true;

		@Config.LangKey("randomtweaks.config.misc.disableBedProximityRequirement")
		@Config.Comment("Disables the bed proximity requirement.")
		public boolean disableBedProximityRequirement = true;

		@Config.RangeDouble(min = 0.0, max = 1024.0)
		@Config.LangKey("randomtweaks.config.misc.attackSpeed")
		@Config.Comment({
				"The base attack speed for players.",
				"Set this to 16.0 or higher to remove the attack cooldown."
		})
		public double attackSpeed = RandomTweaks.IS_DEOBFUSCATED ? 24.0 : 4.0;

		@Config.LangKey("randomtweaks.config.misc.betterButtonNames")
		@Config.Comment("Changes the names of the stone and wooden buttons so that they describe " +
				"their type.")
		public boolean betterButtonNames = RandomTweaks.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.misc.deathPunishmentMode")
		@Config.Comment({
				"When death punishments should be enabled.",
				"When death punishments are enabled, players lose all XP and 3 hunger points " +
						"upon dying.",
				"This is always disabled in creative mode.",
				"This has no effect on hunger if hunger resetting is enabled by " +
						"hunger.respawnResetBehavior."
		})
		public RespawnHandler.DeathPunishmentMode deathPunishmentMode =
				RespawnHandler.DeathPunishmentMode.ENABLED_IF_KEEP_INVENTORY;

		@Config.LangKey("randomtweaks.config.misc.disableCumulativeAnvilCosts")
		@Config.Comment("Disables cumulative anvil costs.")
		public boolean disableCumulativeAnvilCosts = true;

		@Config.LangKey("randomtweaks.config.misc.disableNetherPortalCreationGameRuleName")
		@Config.Comment({
				"The name of the gamerule that disables nether portal creation.",
				"Set this to an empty string to disable this gamerule."
		})
		public String disableNetherPortalCreationGameRuleName = "disableNetherPortalCreation";

		@Config.LangKey("randomtweaks.config.misc.entitiesDropNameTags")
		@Config.Comment("Whether living entities should drop name tags if they have a custom name.")
		public boolean entitiesDropNameTags = RandomTweaks.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.misc.fallDamageMultiplierGameRuleName")
		@Config.Comment({
				"The name of the gamerule that controls the fall damage multiplier.",
				"Set this to an empty string to disable this gamerule."
		})
		public String fallDamageMultiplierGameRuleName = "fallDamageMultiplier";

		@Config.LangKey("randomtweaks.config.misc.farmlandTrampleBehavior")
		@Config.Comment("The farmland trample behavior.")
		public TrampleHandler.Behavior farmlandTrampleBehavior =
				TrampleHandler.Behavior.DONT_TRAMPLE_IF_FEATHER_FALLING;

		@Config.LangKey("randomtweaks.config.misc.moreRomanNumerals")
		@Config.Comment("Generates Roman numerals from " + Short.MIN_VALUE + " to " +
				Short.MAX_VALUE + " as they are needed.")
		public boolean moreRomanNumerals = true;

		@Config.LangKey("randomtweaks.config.misc.pickUpSkeletonArrows")
		@Config.Comment("Allows skeleton arrows to be picked up.")
		public boolean pickUpSkeletonArrows = RandomTweaks.IS_DEOBFUSCATED;
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

	public static class SheepColorWeights {
		@Config.Ignore
		public final Map<EnumDyeColor, Double> weights = new EnumMap<>(EnumDyeColor.class);

		@Config.Ignore
		public double totalWeight;

		@Config.LangKey("randomtweaks.config.sheepColorWeights.enabled")
		@Config.Comment("Enables colored sheep spawning.")
		public boolean enabled = true;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("item.fireworksCharge.white")
		@Config.Comment("The weight for the color white.")
		public double white = 70.0;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("item.fireworksCharge.orange")
		@Config.Comment("The weight for the color orange.")
		public double orange = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("item.fireworksCharge.magenta")
		@Config.Comment("The weight for the color magenta.")
		public double magenta = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("item.fireworksCharge.lightBlue")
		@Config.Comment("The weight for the color light blue.")
		public double lightBlue = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("item.fireworksCharge.yellow")
		@Config.Comment("The weight for the color yellow.")
		public double yellow = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("item.fireworksCharge.lime")
		@Config.Comment("The weight for the color lime.")
		public double lime = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("item.fireworksCharge.pink")
		@Config.Comment("The weight for the color pink.")
		public double pink = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("item.fireworksCharge.gray")
		@Config.Comment("The weight for the color gray.")
		public double gray = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("item.fireworksCharge.silver")
		@Config.Comment("The weight for the color silver.")
		public double silver = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("item.fireworksCharge.cyan")
		@Config.Comment("The weight for the color cyan.")
		public double cyan = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("item.fireworksCharge.purple")
		@Config.Comment("The weight for the color purple.")
		public double purple = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("item.fireworksCharge.blue")
		@Config.Comment("The weight for the color blue.")
		public double blue = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("item.fireworksCharge.brown")
		@Config.Comment("The weight for the color brown.")
		public double brown = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("item.fireworksCharge.green")
		@Config.Comment("The weight for the color green.")
		public double green = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("item.fireworksCharge.red")
		@Config.Comment("The weight for the color red.")
		public double red = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.LangKey("item.fireworksCharge.black")
		@Config.Comment("The weight for the color black.")
		public double black = 2.0;

		private void reload() {
			totalWeight = 0.0;

			putWeight(EnumDyeColor.WHITE, white);
			putWeight(EnumDyeColor.ORANGE, orange);
			putWeight(EnumDyeColor.MAGENTA, magenta);
			putWeight(EnumDyeColor.LIGHT_BLUE, lightBlue);
			putWeight(EnumDyeColor.YELLOW, yellow);
			putWeight(EnumDyeColor.LIME, lime);
			putWeight(EnumDyeColor.PINK, pink);
			putWeight(EnumDyeColor.GRAY, gray);
			putWeight(EnumDyeColor.SILVER, silver);
			putWeight(EnumDyeColor.CYAN, cyan);
			putWeight(EnumDyeColor.PURPLE, purple);
			putWeight(EnumDyeColor.BLUE, blue);
			putWeight(EnumDyeColor.BROWN, brown);
			putWeight(EnumDyeColor.GREEN, green);
			putWeight(EnumDyeColor.RED, red);
			putWeight(EnumDyeColor.BLACK, black);
		}

		private void putWeight(EnumDyeColor color, double weight) {
			totalWeight += weight;
			weights.put(color, weight);
		}
	}

	public static class Squids {
		@Config.RangeInt(min = -1)
		@Config.LangKey("randomtweaks.config.squids.chunkLimit")
		@Config.Comment({
				"The number of squids allowed in one chunk.",
				"Set this to " + SquidHandler.SQUID_SPAWNING_DISABLED +
						" to disable squid spawning.",
				"Set this to " + SquidHandler.CHUNK_LIMIT_DISABLED + " to disable this limit."
		})
		public int chunkLimit = 12;

		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.squids.maxPackSize")
		@Config.Comment({
				"The maximum number of squids that can be spawned in a pack.",
				"Set this to " + SquidHandler.VANILLA_PACK_SIZE + " to use vanilla behavior."
		})
		public int maxPackSize = 2;

		@Config.RangeInt(min = 0)
		@Config.LangKey("randomtweaks.config.squids.spawnRadiusLimit")
		@Config.Comment({
				"Disables squid spawning when a player is not within this radius.",
				"Set this to " + SquidHandler.RADIUS_LIMIT_DISABLED + " to disable this limit."
		})
		public int spawnRadiusLimit = 128;
	}

	public static class TimeOfDay {
		@Config.LangKey("randomtweaks.config.timeOfDay.alignment")
		@Config.Comment("The alignment.")
		public Alignment alignment = Alignment.TOPLEFT;

		@Config.LangKey("randomtweaks.config.timeOfDay.disableIfNoDaylightCycle")
		@Config.Comment("Disables the overlay if doDaylightCycle is false.")
		public boolean disableIfNoDaylightCycle = !RandomTweaks.IS_DEOBFUSCATED;

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
		public boolean enabledByDefault = RandomTweaks.IS_DEOBFUSCATED;

		@Config.LangKey("randomtweaks.config.timeOfDay.twentyFourHourTime")
		@Config.Comment("Enables 24-hour time.")
		public boolean twentyFourHourTime = RandomTweaks.IS_DEOBFUSCATED;

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
		public final OceanFloor oceanFloor = new OceanFloor();

		@Config.LangKey("randomtweaks.config.world.realisticWorldType")
		@Config.Comment({
				"Enables the Realistic world type",
				"Name: REALISTIC"
		})
		public boolean realisticWorldType = true;

		@Config.RangeInt(min = 1)
		@Config.LangKey("randomtweaks.config.world.voidIslandsChunkRarity")
		@Config.Comment({
				"The rarity of non-empty chunks in a Void Islands world.",
				"If this is set to n, there is a 1 in n chance of a chunk being non-empty.",
				"If this is set to " + ChunkGeneratorVoidIslands.ONLY_GENERATE_SPAWN_CHUNK +
						", only the spawn chunk is generated."
		})
		public int voidIslandsChunkRarity = 10;

		@Config.LangKey("randomtweaks.config.world.voidIslandsWorldBiome")
		@Config.Comment({
				"The biome of the void chunks of a Void Islands world.",
				"Leave this empty for randomized biomes."
		})
		public String voidIslandsWorldBiome = "minecraft:plains";

		@Config.LangKey("randomtweaks.config.world.voidIslandsWorldType")
		@Config.Comment({
				"Enables the Void Islands world type",
				"Name: VOIDISLANDS"
		})
		public boolean voidIslandsWorldType = true;

		@Config.LangKey("randomtweaks.config.world.voidWorldBiome")
		@Config.Comment({
				"The biome of a Void world.",
				"Leave this empty for randomized biomes."
		})
		public String voidWorldBiome = "minecraft:plains";

		@Config.LangKey("randomtweaks.config.world.voidWorldBlock")
		@Config.Comment("The block placed in a Void world for players to stand on.")
		public String voidWorldBlock = "minecraft:glass";

		@Config.LangKey("randomtweaks.config.world.voidWorldType")
		@Config.Comment({
				"Enables the Void world type",
				"Name: VOID"
		})
		public boolean voidWorldType = true;

		@Config.RangeInt(min = 1, max = 255)
		@Config.LangKey("randomtweaks.config.world.voidWorldYSpawn")
		@Config.Comment("The Y coordinate of the default spawn point in a Void world.")
		public int voidWorldYSpawn = 17;
	}

	private interface PropertyConsumer {
		void accept(Property property) throws InvocationTargetException, IllegalAccessException;
	}

	@Config.Ignore
	public static final String NAME = RandomTweaks.MOD_ID + "/" + RandomTweaks.MOD_ID;

	@Config.LangKey("randomtweaks.config.animals")
	@Config.Comment("Options related to animals (including villagers).")
	public static final Animals animals = new Animals();

	@Config.LangKey("randomtweaks.config.boneMeal")
	@Config.Comment("Options related to bone meal.")
	public static final BoneMeal boneMeal = new BoneMeal();

	@Config.LangKey("randomtweaks.config.client")
	@Config.Comment("Options related to features that only work client-side.")
	public static final Client client = new Client();

	@Config.LangKey("randomtweaks.config.commands")
	@Config.Comment("Options related to commands.")
	public static final Commands commands = new Commands();

	@Config.LangKey("randomtweaks.config.hunger")
	@Config.Comment("Options related to hunger behavior.")
	public static final Hunger hunger = new Hunger();

	@Config.LangKey("randomtweaks.config.misc")
	@Config.Comment("Options that don't fit into any other categories.")
	public static final Misc misc = new Misc();

	@Config.LangKey("randomtweaks.config.playerHeadDrops")
	@Config.Comment("Options related to player head drops.")
	public static final PlayerHeadDrops playerHeadDrops = new PlayerHeadDrops();

	@Config.LangKey("randomtweaks.config.world")
	@Config.Comment("Options related to world generation.")
	public static final World world = new World();

	@Config.Ignore
	public static final AutoThirdPerson autoThirdPerson = client.autoThirdPerson;

	@Config.Ignore
	public static final CreativeTabs creativeTabs = client.creativeTabs;

	@Config.Ignore
	public static final Ding ding = client.ding;

	@Config.Ignore
	public static final Keybinds keybinds = client.keybinds;

	@Config.Ignore
	public static final RandomizedAges randomizedAges = animals.randomizedAges;

	@Config.Ignore
	public static final SheepColorWeights sheepColorWeights = animals.sheepColorWeights;

	@Config.Ignore
	public static final Squids squids = animals.squids;

	@Config.Ignore
	public static final TimeOfDay timeOfDay = client.timeOfDay;

	@Config.Ignore
	public static final OceanFloor oceanFloor = world.oceanFloor;

	private static final Method GET_CONFIGURATION = RTUtils.findMethod(
			ConfigManager.class, "getConfiguration", "getConfiguration", String.class, String.class
	);

	private static final Map<Property, Object> defaultValues = new HashMap<>();
	private static final Map<Property, String> comments = new HashMap<>();

	private static boolean firstReload = true;

	public static Path getConfig(String name) {
		final Path path = Paths.get("config", RandomTweaks.MOD_ID, name);
		final Path parent = path.getParent();

		try {
			if(parent != null) {
				if(parent.toFile().exists() && parent.toFile().isFile()) {
					Files.delete(parent);
				}

				Files.createDirectories(parent);
			}
		} catch(IOException ex) {
			RTUtils.crashReport("Failed to create parent: " + path, ex);
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
			RTUtils.crashReport("Failed to read file: " + path, ex);
		}

		return null;
	}

	public static JsonObject readJson(Path path) {
		return new JsonParser().parse(readFile(path)).getAsJsonObject();
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(event.getModID().equals(RandomTweaks.MOD_ID)) {
			reload();
		}
	}

	public static void reload() {
		try {
			if(defaultValues.isEmpty()) {
				forEachProperties(property -> {
					if(property.isList()) {
						defaultValues.put(property, property.getDefaults());
					} else {
						defaultValues.put(property, property.getDefault());
					}
				});
			}

			//reload() is only called by CommonProxy and RTConfig
			//Forge syncs the config during mod construction, so this first sync is not necessary
			if(!firstReload) {
				ConfigManager.sync(RandomTweaks.MOD_ID, Config.Type.INSTANCE);
			}

			firstReload = false;

			modifyConfig();
			ConfigManager.sync(RandomTweaks.MOD_ID, Config.Type.INSTANCE);

			//If Minecraft hasn't loaded yet and ConfigManager.sync is called, the default values
			//are reset
			if(!Loader.instance().hasReachedState(LoaderState.AVAILABLE)) {
				for(Map.Entry<Property, Object> entry : defaultValues.entrySet()) {
					final Property property = entry.getKey();

					if(property.isList()) {
						property.setDefaultValues((String[]) entry.getValue());
					} else {
						property.setDefaultValue((String) entry.getValue());
					}
				}
			}

			modifyConfig();
		} catch(Exception ex) {
			RTUtils.crashReport("Error while modifying config", ex);
		}

		if(sheepColorWeights.enabled) {
			sheepColorWeights.reload();
		}

		Data.data = null;

		if(misc.betterButtonNames) {
			Blocks.STONE_BUTTON.setTranslationKey("buttonStone");
			Blocks.WOODEN_BUTTON.setTranslationKey("buttonWood");
		} else {
			Blocks.STONE_BUTTON.setTranslationKey("button");
			Blocks.WOODEN_BUTTON.setTranslationKey("button");
		}
	}

	public static void reloadFromDisk() {
		try {
			final Configuration config =
					(Configuration) GET_CONFIGURATION.invoke(null, RandomTweaks.MOD_ID, NAME);
			final Configuration tempConfig = new Configuration(config.getConfigFile());

			tempConfig.load();

			for(String name : tempConfig.getCategoryNames()) {
				final Map<String, Property> properties = tempConfig.getCategory(name).getValues();

				for(Map.Entry<String, Property> entry : properties.entrySet()) {
					config.getCategory(name).get(entry.getKey()).set(entry.getValue().getString());
				}
			}

			reload();
		} catch(Exception ex) {
			RTUtils.crashReport("Error while modifying config", ex);
		}
	}

	private static void forEachProperties(PropertyConsumer consumer)
			throws InvocationTargetException, IllegalAccessException {
		final Configuration config =
				(Configuration) GET_CONFIGURATION.invoke(null, RandomTweaks.MOD_ID, NAME);

		for(String name : config.getCategoryNames()) {
			final Map<String, Property> properties = config.getCategory(name).getValues();

			for(Property property : properties.values()) {
				consumer.accept(property);
			}
		}
	}

	private static void modifyConfig() throws IllegalAccessException, InvocationTargetException {
		final Configuration config =
				(Configuration) GET_CONFIGURATION.invoke(null, RandomTweaks.MOD_ID, NAME);

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

				String newComment = comments.get(property);

				if(newComment == null) {
					newComment = comment + "\nDefault: " + property.getDefault();
					comments.put(property, newComment);
				}

				property.setComment(newComment);
			});

			if(category.getValues().isEmpty() || category.getComment() == null) {
				config.removeCategory(category);
			}
		}

		config.save();

		//Remove default values, min/max values and valid values from the comments so
		//they don't show up twice in the configuration GUI
		forEachProperties(property -> {
			final String[] comment = property.getComment().split("\n");
			final StringBuilder prunedComment = new StringBuilder();

			for(String line : comment) {
				if(line.startsWith("Default:") || line.startsWith("Min:")) {
					break;
				}

				prunedComment.append(line).append("\n");
			}

			final String commentString = prunedComment.toString();
			property.setComment(commentString.substring(0, commentString.length() - 1));
		});
	}
}
