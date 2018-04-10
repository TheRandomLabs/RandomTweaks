package com.therandomlabs.randomtweaks.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class Compat {
	public static final String ACCEPTED_MINECRAFT_VERSIONS = "[1.10,1.12)";
	public static final boolean IS_ONE_POINT_TEN = isOnePointTen();
	public static final String CHICKEN_ENTITY_NAME = IS_ONE_POINT_TEN ? "Chicken" : "chicken";

	public static final IForgeRegistry<Block> BLOCK_REGISTRY =
			GameRegistry.findRegistry(Block.class);
	public static final IForgeRegistry<Biome> BIOME_REGISTRY =
			GameRegistry.findRegistry(Biome.class);

	public static final Field STACK_SIZE =
			ReflectionHelper.findField(ItemStack.class, "stackSize", "field_77994_a");
	public static final Method ADD_CHAT_COMPONENT_MESSAGE =
			findMethod(EntityPlayer.class, "addChatComponentMessage", "func_146105_b",
					ITextComponent.class);
	public static final Method LOAD =
			findMethod(ConfigManager.class, "load", "load", String.class, Config.Type.class);
	public static final Method CLEAR_CHAT_MESSAGES = isOnePointTen() ?
			findMethod(GuiNewChat.class, "clearChatMessages", "func_146231_a") : null;

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
		//Hacky, I know, and this will cause a crash in a 1.10 development environment
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

	public static void sendStatusMessage(EntityPlayer player, ITextComponent message)
			throws Exception {
		if(IS_ONE_POINT_TEN) {
			ADD_CHAT_COMPONENT_MESSAGE.invoke(player, message);
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

	private static boolean isOnePointTen() {
		try {
			return ((String) MinecraftForge.class.getDeclaredField("MC_VERSION").get(null)).
					startsWith("1.10");
		} catch(Exception ex) {
			Utils.crashReport("RandomTweaks could not retrieve the current Minecraft version", ex);
		}
		return false;
	}
}
