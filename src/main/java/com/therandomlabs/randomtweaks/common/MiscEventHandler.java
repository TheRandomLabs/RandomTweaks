package com.therandomlabs.randomtweaks.common;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import com.therandomlabs.randomlib.EntityUtils;
import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.client.ArmorEquipSoundHandler;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCake;
import net.minecraft.block.BlockSponge;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.ArrayUtils;

@Mod.EventBusSubscriber
public final class MiscEventHandler {
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if(event.getWorld().isRemote) {
			return;
		}

		final Entity entity = event.getEntity();

		if(entity instanceof EntityPlayer) {
			onPlayerJoinWorld((EntityPlayer) entity);
			return;
		}

		if(entity instanceof EntityZombie) {
			ZombieAIHandler.onZombieJoinWorld((EntityZombie) entity);
		}
	}

	public static void onPlayerJoinWorld(EntityPlayer player) {
		final IAttributeInstance attackSpeed =
				player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
		attackSpeed.setBaseValue(RTConfig.Misc.attackSpeed);

		if(RTConfig.Hunger.enabled && !RandomTweaks.APPLECORE_LOADED) {
			player.foodStats = new RTFoodStats(player.foodStats);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
		final Entity entity = event.getEntity();

		if(!(entity instanceof EntityAgeable)) {
			return;
		}

		if(RTConfig.SheepColorWeights.enabled && entity.getClass() == EntitySheep.class) {
			ColoredSheepHandler.onSheepSpawn((EntitySheep) entity);
		}

		if(RTConfig.RandomizedAges.chance != 0.0) {
			final EntityAgeable ageable = (EntityAgeable) entity;

			if(ageable.isChild()) {
				return;
			}

			final Random rng = ageable.getRNG();

			if(rng.nextDouble() < RTConfig.RandomizedAges.chance) {
				final int min = RTConfig.RandomizedAges.minimumAge;
				final int max = RTConfig.RandomizedAges.maximumAge;

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
		final EntityLivingBase entity = event.getEntityLiving();

		if(entity.getEntityWorld().isRemote) {
			return;
		}

		if(RTConfig.Misc.entityNaNHealthFix && Float.isNaN(entity.getHealth())) {
			entity.setHealth(0.0F);
			return;
		}

		if(entity.getClass() == EntitySheep.class) {
			ColoredSheepHandler.onSheepTick((EntitySheep) entity);
		}
	}

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		final EntityLivingBase entity = event.getEntityLiving();
		final DamageSource source = event.getSource();
		final float amount = event.getAmount();

		if(RTConfig.Misc.entityNaNHealthFix && Float.isNaN(amount)) {
			RandomTweaks.LOGGER.error("{} was damaged by a NaN value.", entity);
			RandomTweaks.LOGGER.error("Immediate source: " + source);
			RandomTweaks.LOGGER.error("True source: " + source.getSourceOfDamage());
			RandomTweaks.LOGGER.error(
					"This damage will be canceled. Please report this to the relevant mod author."
			);

			event.setResult(Event.Result.DENY);
			event.setCanceled(true);
			return;
		}

		final String gameRule;

		if(source == DamageSource.drown) {
			gameRule = RTConfig.GameRules.drowningDamageMultiplier;
		} else if(source == DamageSource.fall) {
			gameRule = RTConfig.GameRules.fallDamageMultiplier;
		} else if(source == DamageSource.inFire || source == DamageSource.onFire) {
			gameRule = RTConfig.GameRules.fireDamageMultiplier;
		} else {
			return;
		}

		if(gameRule.isEmpty()) {
			return;
		}

		float multiplier = 0.0F;

		try {
			multiplier = Float.parseFloat(
					entity.getEntityWorld().getGameRules().getString(gameRule)
			);
		} catch(NumberFormatException ignored) {}

		if(multiplier == 0.0F) {
			event.setCanceled(true);
		} else if(multiplier <= 0.0F) {
			event.setCanceled(true);
			entity.setHealth(Math.max(
					entity.getHealth() + amount * multiplier,
					entity.getMaxHealth()
			));
		} else {
			event.setAmount(amount * multiplier);
		}
	}

	@SubscribeEvent
	public static void onLivingAttack(LivingAttackEvent event) {
		final Entity attacker = event.getSource().getEntity();
		final EntityLivingBase entity = event.getEntityLiving();

		if(attacker == null || !(entity instanceof IEntityOwnable)) {
			return;
		}

		final IEntityOwnable pet = ((IEntityOwnable) entity);
		final UUID owner = pet.getOwnerId();

		if(owner == null) {
			return;
		}

		final boolean protectFromSneaking = RTConfig.Animals.protectPetsFromSneakingOwners;

		if(RTConfig.Animals.protectPetsFromOwners && owner.equals(attacker.getUniqueID()) &&
				(protectFromSneaking || (!protectFromSneaking && !attacker.isSneaking()))) {
			event.setCanceled(true);
			return;
		}

		if(RTConfig.Animals.protectPetsFromOtherPets && attacker instanceof IEntityOwnable) {
			final IEntityOwnable otherPet = (IEntityOwnable) attacker;

			if(owner.equals(otherPet.getOwnerId())) {
				entity.setRevengeTarget(null);
				((EntityLivingBase) attacker).setRevengeTarget(null);
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		final EntityLivingBase entity = event.getEntityLiving();

		if(RTConfig.Misc.mobsDropAllArmorAndEquipment && entity instanceof EntityLiving) {
			final EntityLiving living = (EntityLiving) entity;
			Arrays.fill(living.inventoryHandsDropChances, 1.0F);
			Arrays.fill(living.inventoryArmorDropChances, 1.0F);
		}

		if(!RTConfig.Misc.mobsAlwaysDropLoot) {
			return;
		}

		final World world = entity.getEntityWorld();

		if(world.isRemote) {
			return;
		}

		if(entity.recentlyHit == 0) {
			entity.recentlyHit = 100;
		}

		if(entity.attackingPlayer == null) {
			entity.attackingPlayer = FakePlayerFactory.getMinecraft((WorldServer) world);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLivingDrops(LivingDropsEvent event) {
		if(RandomTweaks.VANILLATWEAKS_LOADED) {
			return;
		}

		final Entity entity = event.getEntity();

		if(!entity.getEntityWorld().getGameRules().getBoolean("doMobLoot")) {
			return;
		}

		if(RTConfig.Animals.batLeatherDropChance != 0.0 && entity instanceof EntityBat &&
				Math.random() < RTConfig.Animals.batLeatherDropChance) {
			entity.dropItem(Items.LEATHER, 1);
		}

		if(!RTConfig.Misc.entitiesDropNameTags) {
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

	@SubscribeEvent
	public static void onPlayerAttackEntity(AttackEntityEvent event) {
		if(RandomTweaks.RANDOMCONFIGS_LOADED || !RTConfig.Misc.disableAttacksDuringAttackCooldown) {
			return;
		}

		final EntityPlayer player = event.getEntityPlayer();

		if(!player.getEntityWorld().isRemote && player.getCooledAttackStrength(0.5F) != 1.0F) {
			player.resetCooldown();
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if(!RTConfig.Misc.updateAllMapsInInventory) {
			return;
		}

		final EntityPlayer player = event.player;
		final World world = player.getEntityWorld();

		if(world.isRemote) {
			return;
		}

		//Taken from https://github.com/quat1024/Crowmap/blob/master/src/main/java/quaternary/
		//crowmap/Crowmap.java

		for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
			if(i == player.inventory.currentItem) {
				//The map is already being held, so there's no need to update it again
				return;
			}

			final ItemStack stack = player.inventory.getStackInSlot(i);

			if(stack != null && stack.getItem() == Items.FILLED_MAP) {
				Items.FILLED_MAP.updateMapData(
						world, player, Items.FILLED_MAP.getMapData(stack, world)
				);
			}
		}
	}

	@SubscribeEvent
	public static void onArrowNock(ArrowNockEvent event) {
		if(!RTConfig.Misc.bowInfinityFix) {
			return;
		}

		final ItemStack bow = event.getBow();

		//Taken from https://github.com/Parker8283/BowInfinityFix/blob/master/src/main/java/net/
		//parker8283/bif/BowInfinityFix.java
		if(EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bow) > 0) {
			event.getEntityPlayer().setActiveHand(event.getHand());
			event.setAction(new ActionResult<>(EnumActionResult.SUCCESS, bow));
		}
	}

	@SubscribeEvent
	public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
		if(!RTConfig.Misc.cakeSoundsAndParticles) {
			return;
		}

		final World world = event.getWorld();
		final EntityPlayer player = event.getEntityPlayer();
		final BlockPos pos = event.getPos();
		final IBlockState state = world.getBlockState(pos);
		final Block block = state.getBlock();

		if(!(block instanceof BlockCake) || !player.canEat(false)) {
			return;
		}

		final Random random = player.getRNG();

		final ItemStack stack = block.getPickBlock(state, null, world, pos, player);
		final int id = Item.getIdFromItem(stack.getItem());
		final int meta = stack.getMetadata();

		//Taken from EntityLivingBase#updateItemUse
		for(int i = 0; i < 5; i++) {
			final Vec3d particlePos = new Vec3d(
					(random.nextFloat() - 0.5) * 0.3, (-random.nextFloat()) * 0.6 - 0.3, 0.6
			).rotatePitch(
					-player.rotationPitch * 0.017453292F
			).rotateYaw(
					-player.rotationYaw * 0.017453292F
			).addVector(
					player.posX, player.posY + player.getEyeHeight() + 0.05, player.posZ
			);

			final Vec3d particleSpeed = new Vec3d(
					(random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0
			).rotatePitch(
					-player.rotationPitch * 0.017453292F
			).rotateYaw(
					-player.rotationYaw * 0.017453292F
			);

			world.spawnParticle(
					EnumParticleTypes.ITEM_CRACK,
					particlePos.xCoord, particlePos.yCoord, particlePos.zCoord,
					particleSpeed.xCoord, particleSpeed.yCoord, particleSpeed.zCoord,
					id, meta
			);
		}

		player.playSound(
				SoundEvents.ENTITY_GENERIC_EAT,
				0.5F + 0.5F * random.nextInt(2),
				(random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F
		);
	}

	@SubscribeEvent
	public static void onBlockPlaced(BlockEvent.PlaceEvent event) {
		if(!RTConfig.Misc.wetSpongesDryInNether) {
			return;
		}

		final World world = event.getWorld();

		if(world.isRemote) {
			return;
		}

		final IBlockState state = event.getPlacedBlock();

		if(state.getBlock() != Blocks.SPONGE || !state.getValue(BlockSponge.WET)) {
			return;
		}

		final BlockPos pos = event.getPos();

		if(!ArrayUtils.contains(
				BiomeDictionary.getTypesForBiome(world.getBiome(pos)), BiomeDictionary.Type.NETHER
		)) {
			return;
		}

		world.setBlockState(pos, state.withProperty(BlockSponge.WET, false));

		world.playSound(
				null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.3F,
				2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F
		);

		for(int i = 0; i < 8; i++) {
			world.spawnParticle(
					EnumParticleTypes.SMOKE_NORMAL, pos.getX() + Math.random(),
					pos.getY() + Math.random(), pos.getZ() + Math.random(), 0.0, 0.0, 0.0
			);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
		if(!RTConfig.Misc.armorStandSwapping) {
			return;
		}

		final EntityPlayer player = event.getEntityPlayer();

		if(player.getEntityWorld().isRemote || !player.isSneaking() || player.isSpectator()) {
			return;
		}

		final Entity target = event.getTarget();

		if(!(target instanceof EntityArmorStand)) {
			return;
		}

		event.setCanceled(true);

		final EntityArmorStand armorStand = (EntityArmorStand) target;
		final List<ItemStack> armorStandInventory =
				(List<ItemStack>) armorStand.getArmorInventoryList();

		for(EntityEquipmentSlot slot : EntityUtils.ARMOR_SLOTS) {
			final ItemStack playerStack = player.getItemStackFromSlot(slot);
			final ItemStack armorStandStack = armorStand.getItemStackFromSlot(slot);

			final int index = slot.getIndex();

			player.inventory.armorInventory[index] = armorStandStack;
			armorStandInventory.set(index, playerStack);

			final SoundEvent playerEquipSound = ArmorEquipSoundHandler.getSound(armorStandStack);
			final SoundEvent armorStandEquipSound = ArmorEquipSoundHandler.getSound(playerStack);

			if(playerEquipSound != null) {
				player.playSound(playerEquipSound, 1.0F, 1.0F);
			}

			if(armorStandEquipSound != null) {
				armorStand.playSound(armorStandEquipSound, 1.0F, 1.0F);
			}
		}
	}
}
