package com.therandomlabs.randomtweaks.util;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import com.therandomlabs.randomtweaks.client.MiscClientEventHandler;
import com.therandomlabs.randomtweaks.common.ArrowSpawnHandler;
import com.therandomlabs.randomtweaks.common.RandomTweaks;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
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
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public final class Compat {
	public static final boolean IS_ONE_POINT_TEN = false;
	public static final String ACCEPTED_MINECRAFT_VERSIONS = "[1.12,1.13)";
	public static final String GUI_FACTORY = "";
	public static final String CHICKEN_ENTITY_NAME = "chicken";

	public static final IForgeRegistry<Block> BLOCK_REGISTRY =
			GameRegistry.findRegistry(Block.class);
	public static final IForgeRegistry<Biome> BIOME_REGISTRY =
			GameRegistry.findRegistry(Biome.class);

	public static final Method SPAWN_SHOULDER_ENTITIES =
			findMethod(EntityPlayer.class, "spawnShoulderEntities", "func_192030_dh");

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

	public interface ICompatChunkGenerator extends IChunkGenerator {}

	public static abstract class ICompatWorldGenerator implements IWorldGenerator {
		@Override
		public void generate(Random random, int chunkX, int chunkZ, World world,
				IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
			generate(random, chunkX, chunkZ, world);
		}

		public abstract void generate(Random random, int chunkX, int chunkZ, World world);
	}

	public static class ChunkGeneratorCompatOverworld extends ChunkGeneratorOverworld {
		public ChunkGeneratorCompatOverworld(World world, long seed, boolean mapFeaturesEnabled,
				String generatorOptions) {
			super(world, seed, mapFeaturesEnabled, generatorOptions);
		}
	}

	public static boolean isEmpty(ItemStack stack) {
		return stack.isEmpty();
	}

	public static int getStackSize(ItemStack stack) {
		return stack.getCount();
	}

	public static void setStackSize(ItemStack stack, int size) {
		stack.setCount(size);
	}

	public static void shrinkItemStack(ItemStack stack, int quantity) {
		stack.shrink(quantity);
	}

	public static void sendStatusMessage(EntityPlayer player, ITextComponent message)
			throws Exception {
		player.sendStatusMessage(message, true);
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
		ConfigManager.sync(modid, type);
	}

	public static void clearChatMessages(GuiNewChat chat) {
		chat.clearChatMessages(false);
	}

	public static void spawnShoulderEntities(EntityPlayer player) {
		try {
			SPAWN_SHOULDER_ENTITIES.invoke(player);
		} catch(Exception ex) {
			Utils.crashReport("Could not spawn shoulder entities", ex);
		}
	}

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
				mob -> mob.isPreventingPlayerRest(player) && !mob.hasCustomName()).
				isEmpty();
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

	public static void detectAndSendChanges(Container container) {
		container.detectAndSendChanges();
	}

	public static Block getBlock(String blockName, Block defaultBlock) {
		final Block block = BLOCK_REGISTRY.getValue(new ResourceLocation(blockName));
		return block == null ? defaultBlock : block;
	}

	public static Biome getBiome(String biomeName, Biome defaultBiome) {
		final Biome biome = BIOME_REGISTRY.getValue(new ResourceLocation(biomeName));
		return biome == null ? defaultBiome : biome;
	}

	public static boolean ableToCauseSkullDrop(EntityCreeper creeper) {
		return creeper.ableToCauseSkullDrop();
	}

	public static List<ItemStack> getOres(String oreName) {
		return OreDictionary.getOres(oreName);
	}

	public static void clientInit() {}

	@SubscribeEvent
	public static void onChat(ClientChatEvent event) {
		event.setMessage(MiscClientEventHandler.onChat(event.getMessage()));
	}

	@SubscribeEvent
	public static void onArrowImpact(ProjectileImpactEvent.Arrow event) {
		ArrowSpawnHandler.onArrowImpact(event.getArrow());
	}
}
