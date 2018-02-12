package com.therandomlabs.randomtweaks.common;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = RandomTweaks.MODID)
public final class PetProtectionHandler {
	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		final EntityLivingBase entity = event.getEntityLiving();
		final DamageSource source = event.getSource();

		if(entity == null || !(entity instanceof IEntityOwnable) || source == null) {
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

		if(RTConfig.general.protectPetsFromOwners && owner.equals(attacker.getUniqueID()) &&
				!attacker.isSneaking()) {
			event.setCanceled(true);
			return;
		}

		if(RTConfig.general.protectPetsFromOtherPets && attacker instanceof IEntityOwnable) {
			final IEntityOwnable otherPet = (IEntityOwnable) attacker;
			if(owner.equals(otherPet.getOwnerId())) {
				entity.setRevengeTarget(null);
				((EntityLivingBase) attacker).setRevengeTarget(null);
				event.setCanceled(true);
			}
		}
	}
}
