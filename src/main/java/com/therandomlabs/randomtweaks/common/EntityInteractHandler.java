package com.therandomlabs.randomtweaks.common;

import java.util.Set;
import com.google.common.collect.Sets;
import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RandomTweaks.MODID)
public final class EntityInteractHandler {
	public static final Set<Item> OCELOT_HEAL_ITEMS = Sets.newHashSet(
			Items.FISH
	);
	public static final Set<Item> PARROT_HEAL_ITEMS = Sets.newHashSet(
			Items.WHEAT_SEEDS,
			Items.MELON_SEEDS,
			Items.PUMPKIN_SEEDS,
			Items.BEETROOT_SEEDS
	);

	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		if(event.getWorld().isRemote) {
			return;
		}

		final ItemStack stack = event.getItemStack();

		if(stack.isEmpty()) {
			return;
		}

		final EntityPlayer player = event.getEntityPlayer();
		final Entity target = event.getTarget();

		if(target instanceof EntityVillager) {
			if(RTConfig.animals.leashableVillagers &&
					onVillagerInteract(player, (EntityVillager) target, stack)) {
				event.setCanceled(true);
			}

			return;
		}

		final Set<Item> healItems;

		if(target instanceof EntityOcelot) {
			if(!RTConfig.animals.ocelotsCanBeHealed) {
				return;
			}

			healItems = OCELOT_HEAL_ITEMS;
		} else if(target instanceof EntityParrot) {
			if(!RTConfig.animals.parrotsCanBeHealed) {
				return;
			}

			healItems = PARROT_HEAL_ITEMS;
		} else {
			return;
		}

		final EntityTameable tameable = (EntityTameable) target;
		final int healAmount = getHealAmount(tameable, stack, healItems);

		if(healAmount != 0) {
			event.setCanceled(true);

			if(!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}

			tameable.heal(healAmount);
		}
	}

	private static int getHealAmount(EntityTameable entity, ItemStack stack, Set<Item> healItems) {
		if(!(RandomTweaks.IS_DEOBFUSCATED || entity.isTamed()) ||
				entity.getHealth() >= entity.getMaxHealth()) {
			return 0;
		}

		final Item item = stack.getItem();

		if(!healItems.contains(item)) {
			return 0;
		}

		return ((ItemFood) item).getHealAmount(stack);
	}

	private static boolean onVillagerInteract(EntityPlayer player, EntityVillager villager,
			ItemStack stack) {
		if(villager.getLeashed() && villager.getLeashHolder() == player) {
			villager.clearLeashed(true, !player.capabilities.isCreativeMode);
			return true;
		}

		if(stack.getItem() != Items.LEAD) {
			return false;
		}

		villager.setLeashHolder(player, true);

		if(!player.capabilities.isCreativeMode) {
			stack.shrink(1);
		}

		return true;
	}
}
