package com.therandomlabs.randomtweaks.common;

import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemShears;
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
		if (event.getWorld().isRemote) {
			return;
		}

		final ItemStack stack = event.getItemStack();

		if (stack.isEmpty()) {
			return;
		}

		final EntityPlayer player = event.getEntityPlayer();
		final Entity target = event.getTarget();

		if (target instanceof EntityVillager) {
			onVillagerInteract(player, (EntityVillager) target, stack, event);
			return;
		}

		if (target instanceof EntityCreeper) {
			onCreeperInteract(player, (EntityCreeper) target, stack, event);
			return;
		}

		final Set<Item> healItems;

		if (target instanceof EntityOcelot) {
			if (!RTConfig.Animals.ocelotsCanBeHealed) {
				return;
			}

			healItems = OCELOT_HEAL_ITEMS;
		} else if (target instanceof EntityParrot) {
			if (!RTConfig.Animals.parrotsCanBeHealed) {
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
		if(!entity.isTamed() || entity.getHealth() >= entity.getMaxHealth()) {
			return 0;
		}

		final Item item = stack.getItem();

		if(!healItems.contains(item)) {
			return 0;
		}

		return item instanceof ItemFood ? ((ItemFood) item).getHealAmount(stack) : 1;
	}

	private static void onVillagerInteract(
			EntityPlayer player, EntityVillager villager, ItemStack stack,
			PlayerInteractEvent.EntityInteract event
	) {
		if (!RTConfig.Animals.leashableVillagers) {
			return;
		}

		if (villager.getLeashed() && villager.getLeashHolder() == player) {
			villager.clearLeashed(true, !player.capabilities.isCreativeMode);
			event.setCanceled(true);
			return;
		}

		if (stack.getItem() != Items.LEAD) {
			return;
		}

		villager.setLeashHolder(player, true);

		if (!player.capabilities.isCreativeMode) {
			stack.shrink(1);
		}

		event.setCanceled(true);
	}

	private static void onCreeperInteract(
			EntityPlayer player, EntityCreeper creeper, ItemStack stack,
			PlayerInteractEvent.EntityInteract event
	) {
		if (!RTConfig.Misc.shearableCreepers) {
			return;
		}

		if (creeper.getEntityData().getBoolean("Sheared")) {
			return;
		}

		if (!(stack.getItem() instanceof ItemShears)) {
			return;
		}

		creeper.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);

		final int dropCount = 1 + creeper.getRNG().nextInt(3);
		final Random random = new Random();

		for (int i = 0; i < dropCount; i++) {
			final EntityItem item = creeper.entityDropItem(new ItemStack(Items.GUNPOWDER), 1.0F);
			item.motionY += random.nextFloat() * 0.05F;
			item.motionX += (random.nextFloat() - random.nextFloat()) * 0.1F;
			item.motionZ += (random.nextFloat() - random.nextFloat()) * 0.1F;
		}

		creeper.getEntityData().setBoolean("Sheared", true);
		creeper.targetTasks.taskEntries.clear();
		stack.damageItem(1, player);
	}
}
