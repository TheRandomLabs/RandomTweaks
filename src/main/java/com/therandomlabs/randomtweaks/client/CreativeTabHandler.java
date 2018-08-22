package com.therandomlabs.randomtweaks.client;

import java.lang.reflect.Field;
import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public final class CreativeTabHandler {
	public static final CreativeTabs SPAWN_EGGS = new CreativeTabs("spawnEggs") {
		@SideOnly(Side.CLIENT)
		@Override
		public ItemStack createIcon() {
			final ItemStack stack = new ItemStack(Items.SPAWN_EGG);
			ItemMonsterPlacer.applyEntityIdToItemStack(stack, new ResourceLocation("chicken"));
			return stack;
		}
	};

	public static final Field TAB_PAGE =
			ReflectionHelper.findField(GuiContainerCreative.class, "tabPage");

	private static CreativeTabs originalBucketTab;
	private static boolean bucketSetBefore;

	private static CreativeTabs originalCommandBlockTab;
	private static boolean commandBlockSetBefore;

	private static CreativeTabs originalDragonEggTab;
	private static boolean dragonEggSetBefore;

	private static CreativeTabs originalSpawnEggsTab;
	private static boolean spawnEggsSetBefore;

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.PostConfigChangedEvent event) {
		if(event.getModID().equals(RandomTweaks.MODID)) {
			init();
		}
	}

	public static void init() {
		if(RTConfig.client.creativeTabs.moveBucketCreativeTab) {
			originalBucketTab = Items.BUCKET.getCreativeTab();
			bucketSetBefore = true;
			Items.BUCKET.setCreativeTab(CreativeTabs.TOOLS);
		} else if(bucketSetBefore) {
			Items.BUCKET.setCreativeTab(originalBucketTab);
		}

		if(RTConfig.client.creativeTabs.setCommandBlockCreativeTab) {
			originalCommandBlockTab = Blocks.COMMAND_BLOCK.getCreativeTab();
			commandBlockSetBefore = true;
			Blocks.COMMAND_BLOCK.setCreativeTab(CreativeTabs.REDSTONE);
		} else if(commandBlockSetBefore) {
			Blocks.COMMAND_BLOCK.setCreativeTab(originalCommandBlockTab);
		}

		if(RTConfig.client.creativeTabs.setDragonEggCreativeTab) {
			originalDragonEggTab = Blocks.DRAGON_EGG.getCreativeTab();
			dragonEggSetBefore = true;
			Blocks.DRAGON_EGG.setCreativeTab(CreativeTabs.DECORATIONS);
		} else if(dragonEggSetBefore) {
			Blocks.DRAGON_EGG.setCreativeTab(originalDragonEggTab);
		}

		registerSpawnEggsTab();
	}

	private static void registerSpawnEggsTab() {
		if(RTConfig.client.creativeTabs.spawnEggsCreativeTab) {
			if(!ArrayUtils.contains(CreativeTabs.CREATIVE_TAB_ARRAY, SPAWN_EGGS)) {
				CreativeTabs.CREATIVE_TAB_ARRAY =
						ArrayUtils.add(CreativeTabs.CREATIVE_TAB_ARRAY, SPAWN_EGGS);
				SPAWN_EGGS.index = CreativeTabs.CREATIVE_TAB_ARRAY.length - 1;
			}

			originalSpawnEggsTab = Items.SPAWN_EGG.getCreativeTab();
			spawnEggsSetBefore = true;
			Items.SPAWN_EGG.setCreativeTab(SPAWN_EGGS);

			return;
		}

		if(!spawnEggsSetBefore) {
			return;
		}


		final int index = ArrayUtils.indexOf(CreativeTabs.CREATIVE_TAB_ARRAY, SPAWN_EGGS);

		if(index != ArrayUtils.INDEX_NOT_FOUND) {
			CreativeTabs.CREATIVE_TAB_ARRAY =
					ArrayUtils.remove(CreativeTabs.CREATIVE_TAB_ARRAY, index);
			GuiContainerCreative.selectedTabIndex = CreativeTabs.BUILDING_BLOCKS.index;

			try {
				TAB_PAGE.set(null, 0);
			} catch(Exception ex) {
				Utils.crashReport("Error while disabling creative tab", ex);
			}
		}

		Items.SPAWN_EGG.setCreativeTab(originalSpawnEggsTab);
	}
}
