package com.therandomlabs.randomtweaks.common;

import com.therandomlabs.randomtweaks.util.Compat;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = RandomTweaks.MODID)
public final class OcelotHandler {
	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract event)
			throws Exception {
		if(!ConfigurationHandler.ocelotsCanBeHealed) {
			return;
		}

		final EntityPlayer player = event.getEntityPlayer();
		final ItemStack stack = event.getItemStack();

		if(!player.getEntityWorld().isRemote && event.getTarget() instanceof EntityOcelot) {
			final EntityOcelot ocelot = (EntityOcelot) event.getTarget();

			if(canOcelotBeHealed(ocelot, stack)) {
				if(!player.capabilities.isCreativeMode) {
					Compat.shrinkItemStack(stack, 1);
				}

				ocelot.heal(((ItemFood) Items.FISH).getHealAmount(stack));
			}
		}
	}

	public static boolean canOcelotBeHealed(EntityOcelot ocelot, ItemStack stack) {
		return ocelot.isTamed() && !Compat.isEmpty(stack) &&
				stack.isItemEqual(new ItemStack(Items.FISH)) &&
				ocelot.getHealth() < Utils.getMaxHealth(ocelot);
	}
}
