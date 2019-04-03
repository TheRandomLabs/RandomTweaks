package com.therandomlabs.randomtweaks.config;

import java.util.EnumMap;
import java.util.Map;
import com.therandomlabs.randomlib.TRLUtils;
import com.therandomlabs.randomlib.config.Config;
import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.client.CreativeTabHandler;
import com.therandomlabs.randomtweaks.client.KeyBindingHandler;
import com.therandomlabs.randomtweaks.common.RespawnHandler;
import com.therandomlabs.randomtweaks.common.SquidHandler;
import com.therandomlabs.randomtweaks.common.TrampleHandler;
import com.therandomlabs.randomtweaks.common.world.ChunkGeneratorVoidIslands;
import com.therandomlabs.randomtweaks.common.world.WorldTypeRegistry;
import com.therandomlabs.randomtweaks.util.Alignment;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;

@Config(modid = RandomTweaks.MOD_ID, path = RandomTweaks.MOD_ID + "/" + RandomTweaks.MOD_ID)
public final class RTConfig {
	public static final class Animals {
		@Config.Category("Options related to randomized animal ages.")
		public static final RandomizedAges randomizedAges = null;

		@Config.Category("Sheep color weights.")
		public static final SheepColorWeights sheepColorWeights = null;

		@Config.Category("Options related to squid spawning behavior.")
		public static final Squids squids = new Squids();

		@Config.RangeDouble(min = 0.0, max = 1.0)
		@Config.Property("The chance that a bat drops a piece of leather when killed.")
		public static double batLeatherDropChance = 0.8;

		@Config.Property("Whether villagers should be leashable.")
		public static boolean leashableVillagers = true;

		@Config.Property("Whether tamed ocelots can be healed with fish.")
		public static boolean ocelotsCanBeHealed = true;

		@Config.Property("Whether tamed parrots can be healed with seeds.")
		public static boolean parrotsCanBeHealed = true;

		@Config.Property("Prevents pets from being attacked by their owner's other pets.")
		public static boolean protectPetsFromOtherPets = true;

		@Config.Property({
				"Prevents pets from being attacked by their owners.",
				"By default, owners can still attack their pets while sneaking."
		})
		public static boolean protectPetsFromOwners = true;

		@Config.Property("Prevents pets from being attacked by their owners while sneaking.")
		public static boolean protectPetsFromSneakingOwners;
	}

	public static final class ArmorEquipSounds {
		@Config.Property("Whether to enable the armor equip/unequip sounds.")
		public static boolean enabled = true;

		@Config.Property("The elytra equip/unequip sound.")
		public static SoundEvent elytraSound = SoundEvents.ITEM_ARMOR_EQIIP_ELYTRA;

		@Config.Property("The pumpkin equip/unequip sound.")
		public static SoundEvent pumpkinSound = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;

		@Config.Property("The skull equip/unequip sound.")
		public static SoundEvent skullSound = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
	}

	public static final class AutoThirdPerson {
		@Config.Property("Enables auto-third person upon entering a boat.")
		public static boolean boat = TRLUtils.IS_DEOBFUSCATED;

		@Config.Property("Enables auto-third person upon flying with elytra.")
		public static boolean elytra = true;

		@Config.Property("Enables auto-third person.")
		public static boolean enabled = true;

		@Config.Property("Enables auto-third person upon mounting a horse.")
		public static boolean horse = TRLUtils.IS_DEOBFUSCATED;

		@Config.Property("Enables auto-third person upon entering a minecart.")
		public static boolean minecart = TRLUtils.IS_DEOBFUSCATED;

		@Config.Property("Enables auto-third person upon mounting a pig.")
		public static boolean pig = TRLUtils.IS_DEOBFUSCATED;
	}

	public static final class BoneMeal {
		@Config.RangeInt(min = 0, max = 16)
		@Config.Property({
				"The amount of stages bone meal should cause cacti to grow.",
				"16 stages is a full block."
		})
		public static int cacti = 8;

		@Config.Property("Whether bone meal should grow nether wart.")
		public static boolean netherWart = true;

		@Config.RangeInt(min = 0, max = 16)
		@Config.Property({
				"The amount of stages bone meal should cause sugar canes to grow.",
				"16 stages is a full block."
		})
		public static int sugarCanes = 8;
	}

	public static final class Client {
		@Config.Category("Options related to armor equip/unequip sounds.")
		public static final ArmorEquipSounds armorEquipSounds = null;

		@Config.Category("Options related to the auto-third person feature.")
		public static final AutoThirdPerson autoThirdPerson = null;

		@Config.Category("Options related to creative tabs.")
		public static final CreativeTabs creativeTabs = null;

		@Config.Category("Options related to the 'ding' feature.")
		public static final Ding ding = null;

		@Config.Category("Options related to keybinds.")
		public static final Keybinds keybinds = null;

		@Config.Category("Options related to Night Vision.")
		public static final NightVision nightVision = null;

		@Config.Category("Options related to the time of day overlay.")
		public static final TimeOfDay timeOfDay = null;

		@Config.Property("Removes underwater fog.")
		public static boolean clearWater = true;

		@Config.RequiresWorldReload
		@Config.Property("Whether contributor capes should be enabled.")
		public static boolean contributorCapes = true;

		@Config.Property("Disables the ender dragon death sound.")
		public static boolean disableEnderDragonDeathSound;

		@Config.Property("Disables the potion icons at the top right.")
		public static boolean disablePotionIcons = TRLUtils.IS_DEOBFUSCATED;

		@Config.Property("Stops potion effects from moving GUIs to the right.")
		public static boolean disablePotionShift = true;

		@Config.Property("Disables the wither spawn sound.")
		public static boolean disableWitherSpawnSound;

		@Config.Property("Whether to start the game on the Multiplayer screen.")
		public static boolean startOnMultiplayerScreen = TRLUtils.IS_DEOBFUSCATED;

		@Config.Property("Whether to enable stepup.")
		public static boolean stepup = TRLUtils.IS_DEOBFUSCATED;

		@Config.Property("Whether stepup is enabled by default.")
		public static boolean stepupEnabledByDefault = TRLUtils.IS_DEOBFUSCATED;

		@Config.Property(
				"Whether the toggle states for the time of day overlay, stepup and FoV changes " +
						"should be stored in local/client instead of config/randomtweaks."
		)
		public static boolean storeDataInLocal = true;
	}

	public static final class CobwebBurning {
		@Config.Property("Whether the configured items can be used to burn cobwebs.")
		public static boolean enableBurning = TRLUtils.IS_DEOBFUSCATED;

		@Config.Property("Whether cobweb burning should be disabled if the player is sneaking.")
		public static boolean disableBurningIfSneaking = true;

		@Config.Blacklist("minecraft:air")
		@Config.Property("The items that can be used to burn cobwebs.")
		public static Item[] items = {
				Items.FLINT_AND_STEEL,
				Items.FIRE_CHARGE,
				Item.getItemFromBlock(Blocks.TORCH)
		};

		@Config.RangeInt(min = 0, max = Short.MAX_VALUE)
		@Config.Property("How much the configured non-damageable items should be consumed.")
		public static int consumeAmount = 1;

		@Config.RangeInt(min = 0, max = Short.MAX_VALUE)
		@Config.Property("How much the configured damageable items should be damaged.")
		public static int damageAmount = 1;

		@Config.Property("The sound that should be played when cobwebs are burned.")
		public static SoundEvent burnSound = SoundEvents.ITEM_FLINTANDSTEEL_USE;

		@Config.Property("Whether cobwebs should be flammable.")
		public static boolean flammableCobwebs = TRLUtils.IS_DEOBFUSCATED;

		@Config.RangeInt(min = 0)
		@Config.Property("The encouragement value for burning cobwebs.")
		public static int cobwebFireSpreadSpeed = 15;

		@Config.RangeInt(min = 0)
		@Config.Property("The flammability value for burning cobwebs.")
		public static int cobwebFlammability = 70;

		public static void onReload() {
			if(flammableCobwebs) {
				Blocks.FIRE.setFireInfo(Blocks.WEB, cobwebFireSpreadSpeed, cobwebFlammability);
			} else {
				Blocks.FIRE.setFireInfo(Blocks.WEB, 0, 0);
			}
		}
	}

	public static final class Commands {
		@Config.RequiresWorldReload
		@Config.Property("Enables the /deletegamerule command.")
		public static boolean deletegamerule = true;

		@Config.RequiresMCRestart
		@Config.Property(
				"Enables the client-sided /disconnect command, which leaves the current world."
		)
		public static boolean disconnect = true;

		@Config.RequiresWorldReload
		@Config.Property(
				"Allows /give to accept integer IDs, amounts higher than 64 and " +
						"ore dictionary names prefixed by \"ore:\"."
		)
		public static boolean giveTweaks = true;

		@Config.RequiresWorldReload
		@Config.Property(
				"Changes the /help command to make sure that sorting commands " +
						"doesn't cause an error.")
		public static boolean helpTweaks = true;

		@Config.RequiresWorldReload
		@Config.Property("Enables the /hunger command, which sets a player's hunger level.")
		public static boolean hunger = true;

		@Config.RequiresWorldReload
		@Config.Property("Enables the /rtreload command.")
		public static boolean rtreload = true;

		@Config.RequiresMCRestart
		@Config.Property("Enables the client-sided /rtreloadclient command.")
		public static boolean rtreloadclient = true;
	}

	public static final class CreativeTabs {
		@Config.Property("Moves the bucket to the Tools creative tab.")
		public static boolean moveBucketCreativeTab = true;

		@Config.Property(
				"Adds spawn eggs to the Spawn Eggs creative tab that spawn entities without AIs."
		)
		public static boolean noAISpawnEggs = true;

		@Config.Property("Adds the command blocks to the Redstone creative tab.")
		public static boolean setCommandBlockCreativeTab = true;

		@Config.Property("Adds the dragon egg to the Decorations creative tab.")
		public static boolean setDragonEggCreativeTab = true;

		@Config.Property("Moves spawn eggs to their own creative tab.")
		public static boolean spawnEggsCreativeTab = true;

		public static void onReload() {
			CreativeTabHandler.initialize();
		}
	}

	public static final class Ding {
		@Config.Property(
				"Whether to ignore Dynamic Surroundings' \"Mute when Background\" feature."
		)
		public static boolean ignoreDsurroundMuteWhenBackground = true;

		@Config.RangeDouble(min = 0.5, max = 2.0)
		@Config.Property("The pitch of the sound to play when Minecraft starts.")
		public static double startupSoundPitch = 1.0;

		@Config.RangeDouble(min = 0.0, max = 1.0)
		@Config.Property("The volume of the sound to play when Minecraft starts.")
		public static double startupSoundVolume = 0.25;

		@Config.Property("The registry names of the sounds to play when Minecraft starts.")
		public static SoundEvent[] startupSounds = new SoundEvent[] {
				SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP
		};

		@Config.RangeDouble(min = 0.5, max = 2.0)
		@Config.Property("The pitch of the sound to play when a world loads.")
		public static double worldLoadSoundPitch = 1.0;

		@Config.RangeDouble(min = 0.0, max = 1.0)
		@Config.Property("The volume of the sound to play when a world loads.")
		public static double worldLoadSoundVolume = 0.25;

		@Config.Property("The registry names of the sounds to play when a world loads.")
		public static SoundEvent[] worldLoadSounds = new SoundEvent[] {
				SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP
		};
	}

	public static final class GameRules {
		@Config.Previous("misc.disableNetherPortalCreationGameRuleName")
		@Config.Property({
				"The name of the gamerule that disables nether portal creation.",
				"Set this to an empty string to disable this gamerule."
		})
		public static String disableNetherPortalCreation = "disableNetherPortalCreation";

		@Config.Property({
				"The name of the gamerule that controls the drowning damage multiplier.",
				"Set this to an empty string to disable this gamerule."
		})
		public static String drowningDamageMultiplier = "drowningDamageMultiplier";

		@Config.Previous("misc.fallDamageMultiplierGameRuleName")
		@Config.Property({
				"The name of the gamerule that controls the fall damage multiplier.",
				"Set this to an empty string to disable this gamerule."
		})
		public static String fallDamageMultiplier = "fallDamageMultiplier";

		@Config.Property({
				"The name of the gamerule that controls the fire damage multiplier.",
				"Set this to an empty string to disable this gamerule."
		})
		public static String fireDamageMultiplier = "fireDamageMultiplier";
	}

	public static final class Hunger {
		@Config.Property("Carries any excess hunger level gained by eating over to the saturation.")
		public static boolean carryExcessHungerToSaturation = TRLUtils.IS_DEOBFUSCATED;

		@Config.RequiresMCRestart
		@Config.Property("Enables hunger tweaks.")
		public static boolean enabled = true;

		@Config.RangeDouble(min = 0.0)
		@Config.Property("The hunger exhaustion multiplier.")
		public static double exhaustionMultiplier = TRLUtils.IS_DEOBFUSCATED ? 0.0 : 1.0;

		@Config.RangeInt(min = 1)
		@Config.Property("The maximum hunger level.")
		public static int maximumHungerLevel = TRLUtils.IS_DEOBFUSCATED ? 22 : 20;

		@Config.RangeInt(min = 0)
		@Config.Property("The minimum hunger level on respawn.")
		public static int minimumRespawnHungerLevel = 3;

		@Config.Property({
				"What happens to a player's hunger when they respawn.",
				"This has no effect in creative mode."
		})
		public static RespawnHandler.HungerResetBehavior respawnResetBehavior =
				RespawnHandler.HungerResetBehavior.RESET_UNLESS_KEEP_INVENTORY;

		@Config.RangeDouble(min = 0.0)
		@Config.Property(
				"The value added to the player's food level to calculate the maximum " +
						"saturation level."
		)
		public static double saturationLimit = TRLUtils.IS_DEOBFUSCATED ? 100.0 : 0.0;
	}

	public static final class Keybinds {
		@Config.Property("Whether FoV changes should be enabled by default.")
		public static boolean fovChangesEnabledByDefault = true;

		@Config.Property(
				"Whether a status message should be displayed when FoV changes are toggled."
		)
		public static boolean fovChangesStatusMessage = true;

		@Config.Property("Enables the Reload Sound System keybind.")
		public static boolean reloadSoundSystem = true;

		@Config.Property("Enables the Toggle FoV Changes keybind.")
		public static boolean toggleFoVChanges = true;

		@Config.Property("Enables the Toggle Time of Day Overlay keybind.")
		public static boolean toggleTimeOfDayOverlay = true;

		public static void onReload() {
			KeyBindingHandler.registerKeyBindings();
		}
	}

	public static final class Misc {
		@Config.Property("Allows players to sleep near mobs with custom names.")
		public static boolean allowSleepNearMobsWithCustomNames = true;

		@Config.Property("Whether attacks should be disabled during the attack cooldown.")
		public static boolean disableAttacksDuringAttackCooldown = TRLUtils.IS_DEOBFUSCATED;

		@Config.Property("Disables the bed proximity requirement.")
		public static boolean disableBedProximityRequirement = true;

		@Config.RangeDouble(min = 0.0, max = 1024.0)
		@Config.Property({
				"The base attack speed for players.",
				"Set this to 16.0 or higher to remove the attack cooldown."
		})
		public static double attackSpeed = TRLUtils.IS_DEOBFUSCATED ? 24.0 : 4.0;

		@Config.Property(
				"Changes the names of the stone and wooden buttons so that they describe " +
						"their type."
		)
		public static boolean betterButtonNames = TRLUtils.IS_DEOBFUSCATED;

		@Config.Property(
				"Removes the need for a player to have an arrow in their inventory to use a " +
						"bow enchanted with Infinity."
		)
		public static boolean bowInfinityFix = true;

		@Config.Property({
				"When death punishments should be enabled.",
				"When death punishments are enabled, players lose all XP and 3 hunger points " +
						"upon dying.",
				"This is always disabled in creative mode.",
				"This has no effect on hunger if hunger resetting is enabled by " +
						"hunger.respawnResetBehavior."
		})
		public static RespawnHandler.DeathPunishmentMode deathPunishmentMode =
				RespawnHandler.DeathPunishmentMode.ENABLED_IF_KEEP_INVENTORY;

		@Config.Property("Disables cumulative anvil costs.")
		public static boolean disableCumulativeAnvilCosts = true;

		@Config.Property("Whether living entities should drop name tags if they have a custom " +
				"name.")
		public static boolean entitiesDropNameTags = TRLUtils.IS_DEOBFUSCATED;

		@Config.Property({
				"The farmland trample behavior.",
				"This does not work on versions below 1.12.2."
		})
		public static TrampleHandler.Behavior farmlandTrampleBehavior =
				TrampleHandler.Behavior.DONT_TRAMPLE_IF_FEATHER_FALLING;

		@Config.Property("Whether to localize the Nether Portal, End Portal and End Gateway names.")
		public static boolean localizePortalNames = true;

		@Config.Property("Whether mobs should drop loot even if they are not killed by players.")
		public static boolean mobsAlwaysDropLoot = TRLUtils.IS_DEOBFUSCATED;

		@Config.Property(
				"Generates Roman numerals from " + Short.MIN_VALUE + " to " +
						Short.MAX_VALUE + " as they are needed."
		)
		public static boolean moreRomanNumerals = true;

		@Config.Property({
				"Allows skeleton arrows to be picked up.",
				"This does not work on versions below 1.12.2."
		})
		public static boolean pickUpSkeletonArrows = TRLUtils.IS_DEOBFUSCATED;

		@Config.Property(
				"Whether to update all maps in players' inventories instead of only updating " +
						"currently held maps."
		)
		public static boolean updateAllMapsInInventory = TRLUtils.IS_DEOBFUSCATED;

		public static void onReload() {
			if(betterButtonNames) {
				Blocks.STONE_BUTTON.setTranslationKey("buttonStone");
				Blocks.WOODEN_BUTTON.setTranslationKey("buttonWood");
			} else {
				Blocks.STONE_BUTTON.setTranslationKey("button");
				Blocks.WOODEN_BUTTON.setTranslationKey("button");
			}

			if(!RandomTweaks.RANDOMPORTALS_LOADED) {
				if(localizePortalNames) {
					Blocks.PORTAL.setTranslationKey("netherPortal");
					Blocks.END_PORTAL.setTranslationKey("endPortal");
					Blocks.END_GATEWAY.setTranslationKey("endGateway");
				} else {
					Blocks.PORTAL.setTranslationKey(null);
					Blocks.END_PORTAL.setTranslationKey(null);
					Blocks.END_GATEWAY.setTranslationKey(null);
				}
			}
		}
	}

	public static final class NightVision {
		@Config.RequiresMCRestart
		@Config.Property("Whether to enable the following configuration options.")
		public static boolean enabled = true;

		@Config.RangeDouble(min = 0.0, max = 1.0)
		@Config.Property("THe default brightness of the Night Vision effect.")
		public static float brightness = 1.0F;

		@Config.Property(
				"Whether to disable the flashing effect that shows when the Night Vision effect " +
						"has less than 10 seconds remaining."
		)
		public static boolean disableFlashing = true;

		@Config.Property({
				"Whether the Night Vision brightness should gradually fade instead of abruptly " +
						"stopping.",
				"This requires the flashing effect to be disabled."
		})
		public static boolean fadeOut = true;

		@Config.RangeInt(min = 10, max = 200)
		@Config.Property("How long the Night Vision brightness should fade out for.")
		public static int fadeOutTicks = 20;

		public static float fadeOutRate;

		public static void onReload() {
			fadeOutRate = brightness / fadeOutTicks;
		}
	}

	public static final class OceanFloor {
		@Config.RequiresMCRestart
		@Config.RangeInt(min = 0)
		@Config.Property("The clay spawn chance.")
		public static int clayChance = 65;

		@Config.RequiresMCRestart
		@Config.RangeInt(min = 0)
		@Config.Property("The amount of clay in a vein.")
		public static int clayVeinSize = 32;

		@Config.RequiresMCRestart
		@Config.RangeInt(min = 0)
		@Config.Property("The dirt spawn chance.")
		public static int dirtChance = 30;

		@Config.RequiresMCRestart
		@Config.RangeInt(min = 0)
		@Config.Property("The amount of dirt in a vein.")
		public static int dirtVeinSize = 18;

		@Config.RequiresMCRestart
		@Config.Property("Enables the ocean floor worldgen.")
		public static boolean enabled = true;

		@Config.RequiresMCRestart
		@Config.RangeInt(min = 0)
		@Config.Property("The maximum Y coordinate.")
		public static int maxY = 128;

		@Config.RequiresMCRestart
		@Config.RangeInt(min = 0)
		@Config.Property("The minimum Y coordinate.")
		public static int minY = 20;

		@Config.RequiresMCRestart
		@Config.RangeInt(min = 0)
		@Config.Property("The sand spawn chance.")
		public static int sandChance = 45;

		@Config.RequiresMCRestart
		@Config.RangeInt(min = 0)
		@Config.Property("The amount of sand in a vein.")
		public static int sandVeinSize = 22;
	}

	public static final class PlayerHeadDrops {
		@Config.RangeDouble(min = 0.0, max = 1.0)
		@Config.Property(
				"The player head drop chance when a player is killed by a charged creeper."
		)
		public static double chanceWhenKilledByChargedCreeper = 1.0;

		@Config.RangeDouble(min = 0.0, max = 1.0)
		@Config.Property("The player head drop chance when a player is killed by another player.")
		public static double chanceWhenKilledByPlayer = 1.0;

		@Config.Property("Whether players should drop their heads when they die.")
		public static boolean enabled = true;

		@Config.RangeDouble(min = 0.0, max = 1.0)
		@Config.Property("The normal player head drop chance.")
		public static double normalChance = 1.0;
	}

	public static final class RandomizedAges {
		@Config.RangeDouble(min = 0.0, max = 1.0)
		@Config.Property("The chance that an animal's age is randomized.")
		public static double chance = 0.1;

		@Config.Property("The maximum age in ticks.")
		public static int maximumAge = -6000;

		@Config.Property("The minimum age in ticks.")
		public static int minimumAge = -24000;

		public static void onReload() {
			if(maximumAge < minimumAge) {
				maximumAge = minimumAge;
			}
		}
	}

	public static final class SheepColorWeights {
		public static final Map<EnumDyeColor, Double> weights = new EnumMap<>(EnumDyeColor.class);
		public static double totalWeight;

		@Config.Property("Enables colored sheep spawning.")
		public static boolean enabled = true;

		@Config.RangeDouble(min = 0.0)
		@Config.Property("The weight for the color white.")
		public static double white = 70.0;

		@Config.RangeDouble(min = 0.0)
		@Config.Property("The weight for the color orange.")
		public static double orange = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.Property("The weight for the color magenta.")
		public static double magenta = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.Property("The weight for the color light blue.")
		public static double lightBlue = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.Property("The weight for the color yellow.")
		public static double yellow = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.Property("The weight for the color lime.")
		public static double lime = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.Property("The weight for the color pink.")
		public static double pink = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.Property("The weight for the color gray.")
		public static double gray = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.Property("The weight for the color silver.")
		public static double silver = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.Property("The weight for the color cyan.")
		public static double cyan = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.Property("The weight for the color purple.")
		public static double purple = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.Property("The weight for the color blue.")
		public static double blue = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.Property("The weight for the color brown.")
		public static double brown = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.Property("The weight for the color green.")
		public static double green = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.Property("The weight for the color red.")
		public static double red = 2.0;

		@Config.RangeDouble(min = 0.0)
		@Config.Property("The weight for the color black.")
		public static double black = 2.0;

		public static void onReload() {
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

		private static void putWeight(EnumDyeColor color, double weight) {
			totalWeight += weight;
			weights.put(color, weight);
		}
	}

	public static final class Squids {
		@Config.RangeInt(min = -1)
		@Config.Property({
				"The number of squids allowed in one chunk.",
				"Set this to " + SquidHandler.SQUID_SPAWNING_DISABLED +
						" to disable squid spawning.",
				"Set this to " + SquidHandler.CHUNK_LIMIT_DISABLED + " to disable this limit."
		})
		public static int chunkLimit = 12;

		@Config.RangeInt(min = 0)
		@Config.Property({
				"The maximum number of squids that can be spawned in a pack.",
				"Set this to " + SquidHandler.VANILLA_PACK_SIZE + " to use vanilla behavior."
		})
		public static int maxPackSize = 2;

		@Config.RangeInt(min = 0)
		@Config.Property({
				"Disables squid spawning when a player is not within this radius.",
				"Set this to " + SquidHandler.RADIUS_LIMIT_DISABLED + " to disable this limit."
		})
		public static int spawnRadiusLimit = 128;
	}

	public static final class TimeOfDay {
		@Config.Property("The alignment.")
		public static Alignment alignment = Alignment.TOPLEFT;

		@Config.Property("Disables the overlay if doDaylightCycle is false.")
		public static boolean disableIfNoDaylightCycle = !TRLUtils.IS_DEOBFUSCATED;

		@Config.Property("Disables the overlay in Adventure Mode.")
		public static boolean disableInAdventureMode = true;

		@Config.Property("Disables the overlay in GUIs.")
		public static boolean disableInGUIs = true;

		@Config.Property("Enables the overlay.")
		public static boolean enabled = true;

		@Config.Property("Enables the overlay by default.")
		public static boolean enabledByDefault = TRLUtils.IS_DEOBFUSCATED;

		@Config.Property(
				"Whether the overlay should say \"Light\" or \"Dark\" instead of " +
						"\"Day\" or \"Night\"."
		)
		public static boolean lightOrDark;

		@Config.Property("Enables 24-hour time.")
		public static boolean twentyFourHourTime = TRLUtils.IS_DEOBFUSCATED;

		@Config.Property("The X offset.")
		public static int x = 0;

		@Config.Property("The Y offset.")
		public static int y = 0;
	}

	public static final class VoidWorld {
		@Config.Property({
				"The Void world biome.",
				"Leave this empty for randomized biomes."
		})
		public static Biome biome = Biomes.PLAINS;

		@Config.Property({
				"Enables the Void world type",
				"Name: VOID"
		})
		public static boolean enabled = true;

		@Config.Property("Whether mobs spawn in Void worlds.")
		public static boolean mobSpawning = true;

		@Config.Property("The biomes that cannot be randomly generated in Void worlds.")
		public static Biome[] randomBiomeBlacklist = new Biome[0];

		@Config.Property("The block placed at the spawn point of a Void world.")
		public static Block spawnBlock = Blocks.GLASS;

		@Config.RangeInt(min = 0)
		@Config.Property("The meta value of the block placed at the spawn point of a Void world.")
		public static int spawnBlockMeta = 0;

		@Config.RangeInt(min = 1, max = 255)
		@Config.Property("The Y coordinate of the default spawn point in a Void world.")
		public static int ySpawn = 17;

		public static IBlockState spawnBlockState;

		@SuppressWarnings("deprecation")
		public static void onReload() {
			spawnBlockState = spawnBlock.getStateFromMeta(spawnBlockMeta);
		}
	}

	public static final class VoidIslandsWorld {
		@Config.RangeInt(min = 1)
		@Config.Property({
				"The rarity of non-empty chunks in a Void Islands world.",
				"If this is set to n, there is a 1 in n chance of a chunk being non-empty.",
				"If this is set to " + ChunkGeneratorVoidIslands.ONLY_GENERATE_SPAWN_CHUNK +
						", only the spawn chunk is generated."
		})
		public static int chunkRarity = 10;

		@Config.Property({
				"Enables the Void Islands world type",
				"Name: VOIDISLANDS"
		})
		public static boolean enabled = true;

		@Config.Property({
				"The void chunk biome.",
				"Leave this empty for randomized biomes."
		})
		public static Biome voidChunkBiome;

		@Config.Property(
				"The biomes that cannot be randomly generated in void chunks in Void Islands " +
						"worlds."
		)
		public static Biome[] voidChunkRandomBiomeBlacklist = new Biome[0];
	}

	public static final class World {
		@Config.Category("Options related to ocean floor generation.")
		public static final OceanFloor oceanFloor = null;

		@Config.Category("Options regarding Void worlds.")
		public static final VoidWorld voidWorld = null;

		@Config.Category("Options regarding Void Islands worlds.")
		public static final VoidIslandsWorld voidIslandsWorld = null;

		@Config.Property({
				"Whether to fix duplicate entity UUIDs by reassigning them.",
				"This feature is experimental so worlds should be backed up before this is enabled."
		})
		public static boolean fixDuplicateEntityUUIDs = TRLUtils.IS_DEOBFUSCATED;

		@Config.Property("Whether to log entity UUID reassignments.")
		public static boolean logEntityUUIDReassignments = true;

		@Config.Property({
				"Enables the Realistic world type",
				"Name: REALISTIC"
		})
		public static boolean realisticWorldType = true;

		public static void onReload() {
			WorldTypeRegistry.registerWorldTypes();
		}
	}

	@Config.Category("Options related to animals (including villagers).")
	public static final Animals animals = null;

	@Config.Category("Options related to bone meal.")
	public static final BoneMeal boneMeal = null;

	@Config.Category("Options related to features that only work client-side.")
	public static final Client client = null;

	@Config.Category("Options related to cobweb burning.")
	public static final CobwebBurning cobwebBurning = null;

	@Config.Category("Options related to commands.")
	public static final Commands commands = null;

	@Config.Category("Options related to gamerules.")
	public static final GameRules gameRules = null;

	@Config.Category("Options related to hunger behavior.")
	public static final Hunger hunger = null;

	@Config.Category("Options that don't fit into any other categories.")
	public static final Misc misc = null;

	@Config.Category("Options related to player head drops.")
	public static final PlayerHeadDrops playerHeadDrops = null;

	@Config.Category("Options related to world generation.")
	public static final World world = null;
}
