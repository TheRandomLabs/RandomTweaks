package com.therandomlabs.randomtweaks.client;

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
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent event) throws Exception {
		super.preInit(event);

		if(Utils.isDeobfuscated()) {
			RTConfig.timeofday.enabledByDefault = true;
			RTConfig.reloadConfig();
		}

		if(RTConfig.client.moveBucketCreativeTab) {
			Items.BUCKET.setCreativeTab(CreativeTabs.TOOLS);
		}

		if(RTConfig.client.spawnEggsCreativeTab) {
			createSpawnEggsCreativeTab();
		}

		if(RTConfig.client.contributorCapes) {
			CapeHandler.downloadPlayers();
		}

		if(RTConfig.commands.rtreloadclient) {
			ClientCommandHandler.instance.registerCommand(new CommandRtreloadClient());
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);

		if(RTConfig.client.reloadSoundSystemKeybind) {
			SoundSystemReloadHandler.registerKeyBinding();
		}

		if(RTConfig.client.clearChatKeybind) {
			ClearChatHandler.registerKeyBinding();
		}

		if(RTConfig.client.noclipKeybind) {
			NoclipHandler.registerKeyBinding();
		}

		if(RTConfig.timeofday.enableKeybind) {
			TimeOfDayHandler.registerKeyBinding();
		}
	}

	private static void createSpawnEggsCreativeTab() {
		final CreativeTabs SPAWN_EGGS = new CreativeTabs("spawnEggs") {
			@SideOnly(Side.CLIENT)
			@Override
			public ItemStack getTabIconItem() {
				final ItemStack stack = new ItemStack(Items.SPAWN_EGG);
				ItemMonsterPlacer.applyEntityIdToItemStack(stack,
						new ResourceLocation(Compat.CHICKEN_ENTITY_NAME));
				return stack;
			}

			//In 1.10, getTabIconItem returns an Item, not an ItemStack, so we'll just
			//use the obfuscated name
			@SideOnly(Side.CLIENT)
			public Item func_78016_d() {
				return Items.SPAWN_EGG;
			}
		};

		Items.SPAWN_EGG.setCreativeTab(SPAWN_EGGS);
	}
}
