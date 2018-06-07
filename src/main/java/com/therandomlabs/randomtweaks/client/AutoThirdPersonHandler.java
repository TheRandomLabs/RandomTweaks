package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.base.RTConfig;
import com.therandomlabs.randomtweaks.base.RandomTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public final class AutoThirdPersonHandler {
	private static final Minecraft mc = Minecraft.getMinecraft();

	private static final int THIRD_PERSON = 1;

	private static final int ELYTRA = -1;
	private static final int NO_ENTITY = 0;

	private static int lastID = NO_ENTITY;
	private static int originalPerspective;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if(!RTConfig.autoThirdPerson.enabled || mc.world == null || mc.player == null) {
			return;
		}

		if(mc.player.isElytraFlying()) {
			if(!RTConfig.autoThirdPerson.elytra || lastID == ELYTRA) {
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
				if(!RTConfig.autoThirdPerson.horse) {
					return;
				}
			} else if(entity instanceof EntityPig) {
				if(!RTConfig.autoThirdPerson.pig) {
					return;
				}
			} else if(entity instanceof EntityBoat) {
				if(!RTConfig.autoThirdPerson.boat) {
					return;
				}
			} else if(entity instanceof EntityMinecart) {
				if(!RTConfig.autoThirdPerson.minecart) {
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
