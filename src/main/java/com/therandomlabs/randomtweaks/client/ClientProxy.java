package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.client.command.ClientCommandRegistry;
import com.therandomlabs.randomtweaks.common.CommonProxy;
import com.therandomlabs.randomtweaks.common.RTConfig;
import com.therandomlabs.randomtweaks.util.Compat;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
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

		ClientCommandRegistry.register();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);

		if(RTConfig.client.reloadSoundSystemKeybind) {
			SoundSystemReloadHandler.registerKeyBinding();
		}

		if(RTConfig.timeofday.enableKeybind) {
			TimeOfDayHandler.registerKeyBinding();
		}
	}

	private static void createSpawnEggsCreativeTab() {
		//Compat.CreativeTab will crash in a 1.10 development environment
		if(Utils.isDeobfuscated() && Compat.IS_ONE_POINT_TEN) {
			return;
		}

		final CreativeTabs SPAWN_EGGS = new Compat.CreativeTab("spawnEggs") {
			@SideOnly(Side.CLIENT)
			@Override
			public ItemStack getTabIconItem() {
				final ItemStack stack = new ItemStack(Items.SPAWN_EGG);
				ItemMonsterPlacer.applyEntityIdToItemStack(stack,
						new ResourceLocation(Compat.CHICKEN_ENTITY_NAME));
				return stack;
			}

			@SideOnly(Side.CLIENT)
			@Override
			public Item getTabIconItem110() {
				return Items.SPAWN_EGG;
			}
		};

		Items.SPAWN_EGG.setCreativeTab(SPAWN_EGGS);
	}
}
