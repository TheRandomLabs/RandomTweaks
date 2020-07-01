package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.RandomTweaks;
import com.therandomlabs.randomtweaks.config.RTConfig;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.Flights;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityPig;

public final class AutoThirdPersonHandler {
	private static final Minecraft mc = Minecraft.getMinecraft();

	private static final int THIRD_PERSON = 1;

	private static final int WINGS = -2;
	private static final int ELYTRA = -1;
	private static final int NO_ENTITY = 0;

	private static int lastID = NO_ENTITY;
	private static int originalPerspective;

	private static int wingsFlyingTicks;

	private AutoThirdPersonHandler() {}

	public static void onClientTick() {
		if (!RTConfig.AutoThirdPerson.enabled || mc.world == null || mc.player == null ||
				mc.isGamePaused()) {
			return;
		}

		if (RandomTweaks.WINGS_LOADED && RTConfig.AutoThirdPerson.wings) {
			final Flight flight = Flights.get(mc.player);

			if (flight != null && flight.isFlying()) {
				if (wingsFlyingTicks++ == RTConfig.AutoThirdPerson.wingsFlyingTickDelay) {
					lastID = WINGS;
					originalPerspective = mc.gameSettings.thirdPersonView;
					mc.gameSettings.thirdPersonView = THIRD_PERSON;
				}

				return;
			} else {
				if (lastID == WINGS) {
					lastID = NO_ENTITY;
					mc.gameSettings.thirdPersonView = originalPerspective;
				}

				wingsFlyingTicks = 0;
			}
		} else {
			wingsFlyingTicks = 0;
		}

		if (mc.player.isElytraFlying()) {
			if (!RTConfig.AutoThirdPerson.elytra || lastID == ELYTRA) {
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

			if(entity instanceof AbstractHorse) {
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
