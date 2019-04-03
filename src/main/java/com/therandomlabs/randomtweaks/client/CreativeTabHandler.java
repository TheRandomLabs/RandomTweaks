package com.therandomlabs.randomtweaks.client;

import java.lang.reflect.Field;
import java.util.List;
import com.therandomlabs.randomlib.TRLUtils;
import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

//A lot of this is there just so creative tab configuration options can be toggled in-game
//Worth it? Maybe
@Mod.EventBusSubscriber(Side.CLIENT)
public final class CreativeTabHandler {
	public static final CreativeTabs SPAWN_EGGS = new CreativeTabs("spawnEggs") {
		@Override
		public Item getTabIconItem() {
			return Items.SPAWN_EGG;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public ItemStack getIconItemStack() {
			final ItemStack stack = new ItemStack(Items.SPAWN_EGG);
			ItemMonsterPlacer.applyEntityIdToItemStack(stack, "Chicken");
			return stack;
		}

		@Override
		public void displayAllRelevantItems(List<ItemStack> itemList) {
			super.displayAllRelevantItems(itemList);

			if(!RTConfig.CreativeTabs.noAISpawnEggs) {
				return;
			}

			for(String id : EntityList.ENTITY_EGGS.keySet()) {
				final ItemStack stack = new ItemStack(Items.SPAWN_EGG);

				ItemMonsterPlacer.applyEntityIdToItemStack(stack, id);

				final NBTTagCompound tag = stack.getTagCompound().getCompoundTag("EntityTag");
				tag.setBoolean("NoAI", true);

				itemList.add(stack);
			}
		}
	};

	public static final Field TAB_PAGE = TRLUtils.findField(GuiContainerCreative.class, "tabPage");

	private static CreativeTabs originalBucketTab;
	private static boolean bucketSetBefore;

	private static CreativeTabs originalCommandBlockTab;
	private static boolean commandBlockSetBefore;

	private static CreativeTabs originalDragonEggTab;
	private static boolean dragonEggSetBefore;

	private static CreativeTabs originalSpawnEggsTab;
	private static boolean spawnEggsSetBefore;

	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		final ItemStack stack = event.getItemStack();

		if(stack == null || stack.getItem() != Items.SPAWN_EGG) {
			return;
		}

		final NBTTagCompound stackTag = stack.getTagCompound();

		if(stackTag != null && stackTag.hasKey("EntityTag")) {
			final NBTTagCompound entityTag = stackTag.getCompoundTag("EntityTag");

			if(entityTag.getBoolean("NoAI")) {
				event.getToolTip().add(TRLUtils.localize("spawnEgg.noAI"));
			}
		}
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.PostConfigChangedEvent event) {
		if(event.getModID().equals(RandomTweaks.MOD_ID)) {
			init();
		}
	}

	public static void init() {
		if(RTConfig.CreativeTabs.moveBucketCreativeTab) {
			originalBucketTab = Items.BUCKET.getCreativeTab();
			bucketSetBefore = true;
			Items.BUCKET.setCreativeTab(CreativeTabs.TOOLS);
		} else if(bucketSetBefore) {
			Items.BUCKET.setCreativeTab(originalBucketTab);
		}

		if(RTConfig.CreativeTabs.setCommandBlockCreativeTab) {
			originalCommandBlockTab = Blocks.COMMAND_BLOCK.getCreativeTabToDisplayOn();
			commandBlockSetBefore = true;

			Blocks.COMMAND_BLOCK.setCreativeTab(CreativeTabs.REDSTONE);
			Blocks.CHAIN_COMMAND_BLOCK.setCreativeTab(CreativeTabs.REDSTONE);
			Blocks.REPEATING_COMMAND_BLOCK.setCreativeTab(CreativeTabs.REDSTONE);
		} else if(commandBlockSetBefore) {
			Blocks.COMMAND_BLOCK.setCreativeTab(originalCommandBlockTab);
			Blocks.CHAIN_COMMAND_BLOCK.setCreativeTab(originalCommandBlockTab);
			Blocks.REPEATING_COMMAND_BLOCK.setCreativeTab(originalCommandBlockTab);
		}

		if(RTConfig.CreativeTabs.setDragonEggCreativeTab) {
			originalDragonEggTab = Blocks.DRAGON_EGG.getCreativeTabToDisplayOn();
			dragonEggSetBefore = true;

			Blocks.DRAGON_EGG.setCreativeTab(CreativeTabs.DECORATIONS);
		} else if(dragonEggSetBefore) {
			Blocks.DRAGON_EGG.setCreativeTab(originalDragonEggTab);
		}

		registerSpawnEggsTab();
	}

	private static void registerSpawnEggsTab() {
		if(RTConfig.CreativeTabs.spawnEggsCreativeTab) {
			if(!ArrayUtils.contains(CreativeTabs.CREATIVE_TAB_ARRAY, SPAWN_EGGS)) {
				CreativeTabs.CREATIVE_TAB_ARRAY =
						ArrayUtils.add(CreativeTabs.CREATIVE_TAB_ARRAY, SPAWN_EGGS);
				SPAWN_EGGS.tabIndex = CreativeTabs.CREATIVE_TAB_ARRAY.length - 1;
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
			GuiContainerCreative.selectedTabIndex = CreativeTabs.BUILDING_BLOCKS.tabIndex;

			try {
				TAB_PAGE.set(null, 0);
			} catch(Exception ex) {
				RandomTweaks.LOGGER.error("Error while disabling creative tab", ex);
			}
		}

		Items.SPAWN_EGG.setCreativeTab(originalSpawnEggsTab);
	}
}
