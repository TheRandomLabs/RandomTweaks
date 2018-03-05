package com.therandomlabs.randomtweaks.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import java.util.List;

@EventBusSubscriber(modid = RandomTweaks.MODID)
public final class DropHandler {
	@GameRegistry.ObjectHolder("nuclearcraft:gem_dust")
	public static final Item NC_GEM_DUST = null;
	public static final int NC_SULFUR_META = 6;

	@GameRegistry.ObjectHolder("thermalfoundation:material")
	public static final Item TE_MATERIAL = null;
	public static final int TE_SULFUR_META = 771;

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
		if(!RTConfig.general.dropTESulfur || NC_GEM_DUST == null || TE_MATERIAL == null) {
			return;
		}

		final List<ItemStack> drops = event.getDrops();
		for(int i = 0; i < drops.size(); i++) {
			final ItemStack stack = drops.get(i);
			if(stack.getItem() == NC_GEM_DUST && stack.getMetadata() == NC_SULFUR_META) {
				drops.set(i, new ItemStack(TE_MATERIAL, stack.getCount(), TE_SULFUR_META));
				break;
			}
		}
	}
}
