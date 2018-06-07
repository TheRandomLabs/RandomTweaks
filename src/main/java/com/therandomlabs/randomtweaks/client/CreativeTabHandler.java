package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.base.RTConfig;
import com.therandomlabs.randomtweaks.base.RandomTweaks;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public final class CreativeTabHandler {
	public static final CreativeTabs SPAWN_EGGS = new CreativeTabs("spawnEggs") {
		@SideOnly(Side.CLIENT)
		@Override
		public ItemStack getTabIconItem() {
			final ItemStack stack = new ItemStack(Items.SPAWN_EGG);
			ItemMonsterPlacer.applyEntityIdToItemStack(stack, new ResourceLocation("chicken"));
			return stack;
		}
	};

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.PostConfigChangedEvent event) {
		if(event.getModID().equals(RandomTweaks.MODID)) {
			init();
		}
	}

	public static void init() {
		if(RTConfig.client.moveBucketCreativeTab) {
			Items.BUCKET.setCreativeTab(CreativeTabs.TOOLS);
		} else {
			Items.BUCKET.setCreativeTab(CreativeTabs.MISC);
		}

		registerSpawnEggsTab();
	}

	private static void registerSpawnEggsTab() {
		if(RTConfig.client.spawnEggsCreativeTab) {
			if(!ArrayUtils.contains(CreativeTabs.CREATIVE_TAB_ARRAY, SPAWN_EGGS)) {
				CreativeTabs.CREATIVE_TAB_ARRAY =
						ArrayUtils.add(CreativeTabs.CREATIVE_TAB_ARRAY, SPAWN_EGGS);
				SPAWN_EGGS.tabIndex = CreativeTabs.CREATIVE_TAB_ARRAY.length - 1;
			}

			Items.SPAWN_EGG.setCreativeTab(SPAWN_EGGS);
		} else {
			final int index = ArrayUtils.indexOf(CreativeTabs.CREATIVE_TAB_ARRAY, SPAWN_EGGS);

			if(index != ArrayUtils.INDEX_NOT_FOUND) {
				CreativeTabs.CREATIVE_TAB_ARRAY =
						ArrayUtils.remove(CreativeTabs.CREATIVE_TAB_ARRAY, index);

				if(GuiContainerCreative.selectedTabIndex ==
						CreativeTabs.CREATIVE_TAB_ARRAY.length) {
					GuiContainerCreative.selectedTabIndex--;
				}
			}

			Items.SPAWN_EGG.setCreativeTab(CreativeTabs.MISC);
		}
	}
}
