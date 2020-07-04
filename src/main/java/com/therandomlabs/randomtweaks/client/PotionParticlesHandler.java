package com.therandomlabs.randomtweaks.client;

import java.lang.ref.WeakReference;
import java.util.Collection;

import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;

public final class PotionParticlesHandler {
	public static final String TAG = "PotionParticlesDisabled";

	public static boolean disablePotionParticles = RTConfig.Client.potionParticlesEnabledByDefault;

	private static final Minecraft mc = Minecraft.getMinecraft();
	private static WeakReference<EntityLivingBase> previousEntityReference =
			new WeakReference<>(null);

	public static void toggle() {
		disablePotionParticles = !disablePotionParticles;
	}

	public static void onClientTick() {
		final EntityLivingBase entity;

		if (disablePotionParticles || !(mc.getRenderViewEntity() instanceof EntityLivingBase)) {
			entity = null;
		} else {
			entity = (EntityLivingBase) mc.getRenderViewEntity();
		}

		final EntityLivingBase previousEntity = previousEntityReference.get();

		if (previousEntity != entity) {
			if (previousEntity != null && previousEntity.getEntityData().getBoolean(TAG)) {
				//Restore potion effects.
				final Collection<PotionEffect> effects = previousEntity.getActivePotionEffects();

				if (!effects.isEmpty()) {
					previousEntity.getDataManager().set(
							EntityLivingBase.POTION_EFFECTS,
							PotionUtils.getPotionColorFromEffectList(effects)
					);
				}
			}

			previousEntityReference = new WeakReference<>(entity);
		}

		if (entity != null) {
			entity.getDataManager().set(EntityLivingBase.POTION_EFFECTS, 0);
			entity.getEntityData().setBoolean(TAG, true);
		}
	}
}
