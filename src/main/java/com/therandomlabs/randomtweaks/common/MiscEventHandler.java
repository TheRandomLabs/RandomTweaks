package com.therandomlabs.randomtweaks.common;

import java.util.Random;
import java.util.UUID;
import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RandomTweaks.MODID)
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

		if(!(entity instanceof EntityPlayer)) {
			return;
		}

		final EntityPlayer player = (EntityPlayer) event.getEntity();

		player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).
				setBaseValue(RTConfig.misc.attackSpeed);

		if(!RandomTweaks.APPLECORE_LOADED) {
			player.foodStats = new RTFoodStats(player.foodStats);
		}
	}

	@SubscribeEvent
	public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
		final World world = event.getWorld();

		if(world.isRemote) {
			return;
		}

		final Entity entity = event.getEntity();

		if(RTConfig.misc.requireFullCubeForSpawns) {
			final BlockPos pos = entity.getPosition().down();
			final IBlockState state = world.getBlockState(pos);

			if(!state.isFullCube() ||
					state.getCollisionBoundingBox(world, pos) == Block.NULL_AABB) {
				event.setResult(Event.Result.DENY);
				return;
			}
		}

		final Class<?> clazz = entity.getClass();

		if(clazz == EntitySquid.class) {
			SquidHandler.onSquidSpawn(event);
			return;
		}

		if(RTConfig.animals.coloredSheep && !RandomTweaks.COLORFUL_SHEEP_LOADED &&
				clazz == EntitySheep.class) {
			ColoredSheepHandler.onSheepSpawn((EntitySheep) entity);
		}

		if(RTConfig.randomizedAges.chance != 0.0 && entity instanceof EntityAgeable) {
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
	}

	@SubscribeEvent
	public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		final Entity entity = event.getEntity();

		if(entity.getEntityWorld().isRemote) {
			return;
		}

		if(RTConfig.animals.coloredSheep && !RandomTweaks.COLORFUL_SHEEP_LOADED &&
				entity.getClass() == EntitySheep.class) {
			ColoredSheepHandler.onSheepTick((EntitySheep) entity);
		}
	}

	@SubscribeEvent
	public static void onPortalSpawn(BlockEvent.PortalSpawnEvent event) {
		final String name = RTConfig.misc.disableNetherPortalCreationGameruleName;

		if(name.isEmpty()) {
			return;
		}

		final World world = event.getWorld();

		if(world.isRemote) {
			return;
		}

		if(world.getGameRules().getBoolean(name)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		final EntityLivingBase entity = event.getEntityLiving();

		if(entity.getEntityWorld().isRemote) {
			return;
		}

		final DamageSource source = event.getSource();

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

		if(RTConfig.animals.protectPetsFromOwners && owner.equals(attacker.getUniqueID()) &&
				!attacker.isSneaking()) {
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
		final World world = entity.getEntityWorld();

		if(world.isRemote || !world.getGameRules().getBoolean("doMobLoot")) {
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
