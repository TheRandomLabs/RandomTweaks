package com.therandomlabs.randomtweaks.common;

import java.util.Random;
import java.util.UUID;
import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RandomTweaks.MOD_ID)
public final class MiscEventHandler {
	@SubscribeEvent
	public static void onArrowImpact(ProjectileImpactEvent.Arrow event) {
		if(!RTConfig.misc.pickUpSkeletonArrows) {
			return;
		}

		final EntityArrow arrow = event.getArrow();

		if(!arrow.getEntityWorld().isRemote && arrow.shootingEntity instanceof EntitySkeleton &&
				arrow.pickupStatus == EntityArrow.PickupStatus.DISALLOWED) {
			arrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if(event.getWorld().isRemote) {
			return;
		}

		final Entity entity = event.getEntity();

		if(entity instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) event.getEntity();

			final IAttributeInstance attackSpeed =
					player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
			attackSpeed.setBaseValue(RTConfig.misc.attackSpeed);

			if(RTConfig.hunger.enabled && !RandomTweaks.APPLECORE_LOADED) {
				player.foodStats = new RTFoodStats(player.foodStats);
			}

			return;
		}

		if(!(entity instanceof EntityAgeable)) {
			return;
		}

		if(RTConfig.randomizedAges.chance != 0.0) {
			final EntityAgeable ageable = (EntityAgeable) entity;

			if(ageable.isChild()) {
				return;
			}

			final Random rng = ageable.getRNG();

			if(rng.nextDouble() < RTConfig.randomizedAges.chance) {
				final int min = RTConfig.randomizedAges.minimumAge;
				final int max = RTConfig.randomizedAges.maximumAge;

				if(min == max) {
					ageable.setGrowingAge(min);
				} else {
					ageable.setGrowingAge(rng.nextInt(max + 1 - min) + min);
				}
			}
		}

		if(RTConfig.sheepColorWeights.enabled && !RandomTweaks.COLORFUL_SHEEP_LOADED &&
				entity.getClass() == EntitySheep.class) {
			ColoredSheepHandler.onSheepSpawn((EntitySheep) entity);
		}
	}

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		final EntityLivingBase entity = event.getEntityLiving();
		final DamageSource source = event.getSource();

		final String fallDamage = RTConfig.misc.fallDamageMultiplierGameRuleName;

		if(source == DamageSource.FALL && !fallDamage.isEmpty()) {
			float multiplier = 0.0F;

			try {
				multiplier = Float.parseFloat(
						entity.getEntityWorld().getGameRules().getString(fallDamage)
				);
			} catch(NumberFormatException ignored) {}

			if(multiplier == 0.0F) {
				event.setCanceled(true);
			} else if(multiplier <= 0.0F) {
				event.setCanceled(true);
				entity.setHealth(Math.max(
						entity.getHealth() + event.getAmount() * multiplier,
						entity.getMaxHealth()
				));
			} else {
				event.setAmount(event.getAmount() * multiplier);
			}

			return;
		}

		if(!(entity instanceof IEntityOwnable) || source == null) {
			return;
		}

		final Entity attacker = source.getTrueSource();

		if(attacker == null) {
			return;
		}

		final IEntityOwnable pet = ((IEntityOwnable) entity);
		final UUID owner = pet.getOwnerId();

		if(owner == null) {
			return;
		}

		final boolean protectFromSneaking = RTConfig.animals.protectPetsFromSneakingOwners;

		if(RTConfig.animals.protectPetsFromOwners && owner.equals(attacker.getUniqueID()) &&
				(!protectFromSneaking || (protectFromSneaking && !attacker.isSneaking()))) {
			event.setCanceled(true);
			return;
		}

		if(RTConfig.animals.protectPetsFromOtherPets && attacker instanceof IEntityOwnable) {
			final IEntityOwnable otherPet = (IEntityOwnable) attacker;

			if(owner.equals(otherPet.getOwnerId())) {
				entity.setRevengeTarget(null);
				((EntityLivingBase) attacker).setRevengeTarget(null);
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingDrops(LivingDropsEvent event) {
		if(RandomTweaks.VANILLATWEAKS_LOADED) {
			return;
		}

		final Entity entity = event.getEntity();

		if(!entity.getEntityWorld().getGameRules().getBoolean("doMobLoot")) {
			return;
		}

		if(RTConfig.animals.batLeatherDropChance != 0.0 && entity instanceof EntityBat &&
				Math.random() < RTConfig.animals.batLeatherDropChance) {
			entity.dropItem(Items.LEATHER, 1);
		}

		if(!RTConfig.misc.entitiesDropNameTags) {
			return;
		}

		final String customName = entity.getCustomNameTag();

		if(customName.isEmpty()) {
			return;
		}

		final ItemStack nameTag = new ItemStack(Items.NAME_TAG);
		nameTag.setStackDisplayName(customName);
		entity.entityDropItem(nameTag, 0.0F);
	}
}
