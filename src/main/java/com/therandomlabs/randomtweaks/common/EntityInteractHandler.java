package com.therandomlabs.randomtweaks.common;

import java.util.Set;
import com.google.common.collect.ImmutableSet;
import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.config.RTConfig;
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

@Mod.EventBusSubscriber(modid = RandomTweaks.MOD_ID)
public final class EntityInteractHandler {
	public static final ImmutableSet<Item> OCELOT_HEAL_ITEMS = ImmutableSet.of(
			Items.FISH
	);

	public static final ImmutableSet<Item> PARROT_HEAL_ITEMS = ImmutableSet.of(
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
			onVillagerInteract(player, (EntityVillager) target, stack, event);
			return;
		}

		final Set<Item> healItems;

		if(target instanceof EntityOcelot) {
			if(!RTConfig.Animals.ocelotsCanBeHealed) {
				return;
			}

			healItems = OCELOT_HEAL_ITEMS;
		} else if(target instanceof EntityParrot) {
			if(!RTConfig.Animals.parrotsCanBeHealed) {
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
		//If the entity is not tamed and this is not a development environment
		if(!(RandomTweaks.IS_DEOBFUSCATED || entity.isTamed())) {
			return 0;
		}

		if(entity.getHealth() >= entity.getMaxHealth()) {
			return 0;
		}

		final Item item = stack.getItem();

		if(!healItems.contains(item)) {
			return 0;
		}

		return ((ItemFood) item).getHealAmount(stack);
	}

	private static void onVillagerInteract(EntityPlayer player, EntityVillager villager,
			ItemStack stack, PlayerInteractEvent.EntityInteract event) {
		if(!RTConfig.Animals.leashableVillagers) {
			return;
		}

		if(villager.getLeashed() && villager.getLeashHolder() == player) {
			villager.clearLeashed(true, !player.capabilities.isCreativeMode);
			event.setCanceled(true);
			return;
		}

		if(stack.getItem() != Items.LEAD) {
			return;
		}

		villager.setLeashHolder(player, true);

		if(!player.capabilities.isCreativeMode) {
			stack.shrink(1);
		}

		event.setCanceled(true);
	}
}
