package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.common.CommonProxy;
import com.therandomlabs.randomtweaks.common.RTConfig;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent event) throws Exception {
		super.preInit(event);

		if(RTConfig.client.moveBucketCreativeTab) {
			Items.BUCKET.setCreativeTab(CreativeTabs.TOOLS);
		}

		if(RTConfig.client.spawnEggsCreativeTab) {
			createSpawnEggsCreativeTab();
		}

		if(RTConfig.client.contributorCapes) {
			CapeHandler.downloadPlayers();
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);

		if(RTConfig.client.reloadSoundSystemKeybind) {
			SoundSystemReloadHandler.registerKeyBinding();
		}
	}

	private static void createSpawnEggsCreativeTab() {
		final CreativeTabs SPAWN_EGGS = new CreativeTabs("spawnEggs") {
			@SideOnly(Side.CLIENT)
			@Override
			public ItemStack getTabIconItem() {
				final ItemStack stack = new ItemStack(Items.SPAWN_EGG);
				ItemMonsterPlacer.applyEntityIdToItemStack(stack,
						new ResourceLocation("chicken"));
				return stack;
			}
		};

		Items.SPAWN_EGG.setCreativeTab(SPAWN_EGGS);
	}
}
