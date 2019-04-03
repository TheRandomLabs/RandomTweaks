package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;

public final class AutoThirdPersonHandler {
	private static final Minecraft mc = Minecraft.getMinecraft();

	private static final int THIRD_PERSON = 1;

	private static final int ELYTRA = -1;
	private static final int NO_ENTITY = 0;

	private static int lastID = NO_ENTITY;
	private static int originalPerspective;

	private AutoThirdPersonHandler() {}

	public static void onClientTick() {
		if(!RTConfig.AutoThirdPerson.enabled || mc.world == null || mc.player == null) {
			return;
		}

		if(mc.player.isElytraFlying()) {
			if(!RTConfig.AutoThirdPerson.elytra || lastID == ELYTRA) {
				return;
			}

			lastID = ELYTRA;
			originalPerspective = mc.gameSettings.thirdPersonView;
			mc.gameSettings.thirdPersonView = THIRD_PERSON;

			return;
		}

		if(mc.player.isRiding()) {
			final Entity entity = mc.player.getRidingEntity();

			if(lastID == entity.getEntityId()) {
				return;
			}

			if(lastID == NO_ENTITY) {
				originalPerspective = mc.gameSettings.thirdPersonView;
			}

			lastID = entity.getEntityId();

			if(entity instanceof EntityHorse) {
				if(!RTConfig.AutoThirdPerson.horse) {
					return;
				}
			} else if(entity instanceof EntityPig) {
				if(!RTConfig.AutoThirdPerson.pig) {
					return;
				}
			} else if(entity instanceof EntityBoat) {
				if(!RTConfig.AutoThirdPerson.boat) {
					return;
				}
			} else if(entity instanceof EntityMinecart) {
				if(!RTConfig.AutoThirdPerson.minecart) {
					return;
				}
			} else {
				lastID = NO_ENTITY;
				mc.gameSettings.thirdPersonView = originalPerspective;
				return;
			}

			mc.gameSettings.thirdPersonView = THIRD_PERSON;

			return;
		}

		if(lastID != NO_ENTITY) {
			lastID = NO_ENTITY;
			mc.gameSettings.thirdPersonView = originalPerspective;
		}
	}
}
