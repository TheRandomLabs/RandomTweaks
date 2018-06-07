package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.base.RTConfig;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class CreativeTabHandler {
	private CreativeTabHandler() {}

	public static void init() {
		if(RTConfig.client.moveBucketCreativeTab) {
			Items.BUCKET.setCreativeTab(CreativeTabs.TOOLS);
		}

		if(RTConfig.client.spawnEggsCreativeTab) {
			createSpawnEggsTab();
		}
	}

	private static void createSpawnEggsTab() {
		final CreativeTabs SPAWN_EGGS = new CreativeTabs("spawnEggs") {
			@SideOnly(Side.CLIENT)
			@Override
			public ItemStack getTabIconItem() {
				final ItemStack stack = new ItemStack(Items.SPAWN_EGG);
				ItemMonsterPlacer.applyEntityIdToItemStack(stack, new ResourceLocation("chicken"));
				return stack;
			}
		};

		Items.SPAWN_EGG.setCreativeTab(SPAWN_EGGS);
	}
}
