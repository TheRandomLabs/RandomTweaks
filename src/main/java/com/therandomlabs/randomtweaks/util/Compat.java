package com.therandomlabs.randomtweaks.util;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.therandomlabs.randomtweaks.client.MiscClientEventHandler;
import com.therandomlabs.randomtweaks.common.ArrowSpawnHandler;
import com.therandomlabs.randomtweaks.common.RandomTweaks;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderOverworld;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.ThrowableImpactEvent;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public final class Compat {
	public static final boolean IS_ONE_POINT_TEN = isOnePointTen();

	public static final String ACCEPTED_MINECRAFT_VERSIONS = "[1.10,1.12)";
	public static final String GUI_FACTORY =
			"com.therandomlabs.randomtweaks.util.GuiConfigFactoryRandomTweaks";
	public static final String CHICKEN_ENTITY_NAME = IS_ONE_POINT_TEN ? "Chicken" : "chicken";

	public static final IForgeRegistry<Block> BLOCK_REGISTRY =
			GameRegistry.findRegistry(Block.class);
	public static final IForgeRegistry<Biome> BIOME_REGISTRY =
			GameRegistry.findRegistry(Biome.class);

	public static final Field CONFIGS = ReflectionHelper.findField(ConfigManager.class, "CONFIGS");
	public static final Field STACK_SIZE =
			ReflectionHelper.findField(ItemStack.class, "stackSize", "field_77994_a");
	public static final Method ADD_CHAT_COMPONENT_MESSAGE = findMethod(EntityPlayer.class,
			"addChatComponentMessage", "func_146105_b", ITextComponent.class);
	public static final Method LOAD =
			findMethod(ConfigManager.class, "load", "load", String.class, Config.Type.class);
	public static final Method CLEAR_CHAT_MESSAGES = IS_ONE_POINT_TEN ?
			findMethod(GuiNewChat.class, "clearChatMessages", "func_146231_a") : null;
	public static final Method GET_ORES = IS_ONE_POINT_TEN ?
			findMethod(OreDictionary.class, "getOres", "getOres", String.class) : null;

	public static class GuiConfigRandomTweaks extends GuiConfig {
		public GuiConfigRandomTweaks(GuiScreen parentScreen) {
			super(parentScreen, getConfigElements(), RandomTweaks.MODID, false, false,
					Utils.localize(RandomTweaks.MODID + ".config.title"));
		}

		public static List<IConfigElement> getConfigElements() {
			final Configuration configuration = getConfiguration();

			final ConfigCategory topLevelCategory =
					configuration.getCategory(Configuration.CATEGORY_GENERAL);
			topLevelCategory.getChildren().forEach(configCategory ->
					configCategory.setLanguageKey(RandomTweaks.MODID + ".category." +
							configCategory.getName()));

			return new ConfigElement(topLevelCategory).getChildElements();
		}

		public static Configuration getConfiguration() {
			try {
				@SuppressWarnings("unchecked")
				final Map<String, Configuration> configs =
						(Map<String, Configuration>) CONFIGS.get(null);
				for(Map.Entry<String, Configuration> config : configs.entrySet()) {
					if((RandomTweaks.MODID + ".cfg").equals(new File(config.getKey()).getName())) {
						return config.getValue();
					}
				}
			} catch(Exception ex) {
				Utils.crashReport("Could not get configuration", ex);
			}

			return null;
		}
	}

	public static class ClientChatHandler {
		@SubscribeEvent
		public void onChat(ClientChatEvent event) {
			event.setMessage(MiscClientEventHandler.onChat(event.getMessage()));
		}
	}

	public static abstract class CreativeTab extends CreativeTabs {
		public CreativeTab(String label) {
			super(label);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public abstract ItemStack getTabIconItem();

		@SideOnly(Side.CLIENT)
		public abstract Item getTabIconItem110();

		//In 1.10, getTabIconItem returns an Item, not an ItemStack, so we use the obfuscated name
		//Hacky, I know, and this won't work in a 1.10 development environment
		@SideOnly(Side.CLIENT)
		public final Item func_78016_d() {
			return getTabIconItem110();
		}
	}

	public interface ICompatChunkGenerator extends IChunkGenerator {
		boolean isInsideStructure(World world, String structureName, BlockPos pos);
	}

	public static abstract class ICompatWorldGenerator implements IWorldGenerator {
		@Override
		public void generate(Random random, int chunkX, int chunkZ, World world,
				IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
			generate(random, chunkX, chunkZ, world);
		}

		public abstract void generate(Random random, int chunkX, int chunkZ, World world);
	}

	public static class ChunkGeneratorCompatOverworld extends ChunkProviderOverworld {
		public ChunkGeneratorCompatOverworld(World world, long seed, boolean mapFeaturesEnabled,
				String generatorOptions) {
			super(world, seed, mapFeaturesEnabled, generatorOptions);
		}
	}

	public static boolean isEmpty(ItemStack stack) {
		return IS_ONE_POINT_TEN ? stack == null : stack.isEmpty();
	}

	public static int getStackSize(ItemStack stack) throws Exception {
		return IS_ONE_POINT_TEN ? (int) STACK_SIZE.get(stack) : stack.getCount();
	}

	public static void setStackSize(ItemStack stack, int size) throws Exception {
		if(IS_ONE_POINT_TEN) {
			STACK_SIZE.set(stack, size);
		} else {
			stack.setCount(size);
		}
	}

	public static void shrinkItemStack(ItemStack stack, int quantity) throws Exception {
		if(IS_ONE_POINT_TEN) {
			STACK_SIZE.set(stack, (int) STACK_SIZE.get(stack) - quantity);
		} else {
			stack.shrink(quantity);
		}
	}

	public static void sendStatusMessage(EntityPlayer player, ITextComponent message) {
		if(IS_ONE_POINT_TEN) {
			try {
				ADD_CHAT_COMPONENT_MESSAGE.invoke(player, message);
			} catch(Exception ex) {
				Utils.crashReport("Failed to send status message", ex);
			}
		} else {
			player.sendStatusMessage(message, true);
		}
	}

	public static Method findMethod(Class<?> clazz, String methodName, String obfuscatedName,
			Class<?>... parameterTypes) {
		final String nameToFind;
		if(obfuscatedName == null || Utils.isDeobfuscated()) {
			nameToFind = methodName;
		} else {
			nameToFind = obfuscatedName;
		}

		try {
			final Method method = clazz.getDeclaredMethod(nameToFind, parameterTypes);
			method.setAccessible(true);
			return method;
		} catch(NoSuchMethodException ex) {
			return null;
		} catch(Exception ex) {
			throw new UnableToFindMethodException(ex);
		}
	}

	public static void syncConfig(String modid, Config.Type type) {
		if(IS_ONE_POINT_TEN) {
			try {
				LOAD.invoke(null, modid, type);
			} catch(Exception ex) {
				Utils.crashReport("Could not sync config", ex);
			}
		} else {
			ConfigManager.sync(modid, type);
		}
	}

	public static void clearChatMessages(GuiNewChat chat) {
		if(IS_ONE_POINT_TEN) {
			try {
				CLEAR_CHAT_MESSAGES.invoke(chat);
			} catch(Exception ex) {
				Utils.crashReport("Could not clear chat mesages", ex);
			}
		} else {
			chat.clearChatMessages(false);
		}
	}

	public static void spawnShoulderEntities(EntityPlayer player) {}

	public static boolean isMobInRange(EntityPlayer player, World world, BlockPos position) {
		return !world.getEntitiesWithinAABB(EntityMob.class,
				new AxisAlignedBB(
						position.getX(),
						position.getY(),
						position.getZ(),
						position.getX(),
						position.getY(),
						position.getZ()
				).expand(8.0, 5.0, 8.0),
				mob -> isZombiePigmanAngry(mob) && !mob.hasCustomName()).
				isEmpty();
	}

	private static boolean isZombiePigmanAngry(Entity pigman) {
		return !(pigman instanceof EntityPigZombie) || ((EntityPigZombie) pigman).isAngry();
	}

	public static String buildString(String[] args, int startIndex) {
		final StringBuilder stringBuilder = new StringBuilder();
		for(int i = startIndex; i < args.length; i++) {
			if(i > startIndex) {
				stringBuilder.append(" ");
			}
			stringBuilder.append(args[i]);
		}
		return stringBuilder.toString();
	}

	public static void detectAndSendChanges(Container container) {}

	public static Block getBlock(String blockName, Block defaultBlock) {
		final Block block = BLOCK_REGISTRY.getValue(new ResourceLocation(blockName));
		return block == null ? defaultBlock : block;
	}

	public static Biome getBiome(String biomeName, Biome defaultBiome) {
		final Biome biome = BIOME_REGISTRY.getValue(new ResourceLocation(biomeName));
		return biome == null ? defaultBiome : biome;
	}

	public static boolean ableToCauseSkullDrop(EntityCreeper creeper) {
		return creeper.isAIEnabled();
	}

	@SuppressWarnings("unchecked")
	public static List<ItemStack> getOres(String oreName) {
		if(IS_ONE_POINT_TEN) {
			try {
				return (List<ItemStack>) GET_ORES.invoke(null, oreName);
			} catch(Exception ex) {
				Utils.crashReport("Could not retrieve ore dictionary entries", ex);
			}
		}

		return OreDictionary.getOres(oreName);
	}

	public static void clientInit() {
		if(!IS_ONE_POINT_TEN) {
			MinecraftForge.EVENT_BUS.register(new ClientChatHandler());
		}
	}

	@SubscribeEvent
	public static void onArrowImpact(ThrowableImpactEvent event) {
		if(event.getEntity() instanceof EntityArrow) {
			ArrowSpawnHandler.onArrowImpact((EntityArrow) event.getEntity());
		}
	}

	private static boolean isOnePointTen() {
		try {
			return ((String) MinecraftForge.class.getDeclaredField("MC_VERSION").get(null)).
					startsWith("1.10");
		} catch(Exception ex) {
			Utils.crashReport("Could not retrieve Minecraft version", ex);
		}
		return false;
	}
}
