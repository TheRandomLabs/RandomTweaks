package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.common.CommonProxy;
import com.therandomlabs.randomtweaks.common.ConfigurationHandler;
import com.therandomlabs.randomtweaks.util.Compat;
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

		if(ConfigurationHandler.moveBucketCreativeTab) {
			Items.BUCKET.setCreativeTab(CreativeTabs.TOOLS);
		}

		if(ConfigurationHandler.spawnEggsCreativeTab) {
			final CreativeTabs SPAWN_EGGS = new CreativeTabs("spawnEggs") {
				@SideOnly(Side.CLIENT)
				@Override
				public ItemStack getTabIconItem() {
					final ItemStack stack = new ItemStack(Items.SPAWN_EGG);
					ItemMonsterPlacer.applyEntityIdToItemStack(stack,
							new ResourceLocation(Compat.CHICKEN_ENTITY_NAME));
					return stack;
				}
			};

			Items.SPAWN_EGG.setCreativeTab(SPAWN_EGGS);
		}

		if(ConfigurationHandler.contributorCapes) {
			CapeHandler.downloadPlayers();
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);

		if(ConfigurationHandler.reloadSoundSystemKeyBind) {
			SoundSystemReloadHandler.registerKeyBinding();
		}
	}
}
