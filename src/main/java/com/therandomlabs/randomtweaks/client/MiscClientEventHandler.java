package com.therandomlabs.randomtweaks.client;

import com.therandomlabs.randomtweaks.base.RTConfig;
import com.therandomlabs.randomtweaks.base.RandomTweaks;
import net.minecraft.block.material.Material;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = RandomTweaks.MODID)
public final class MiscClientEventHandler {
	@SubscribeEvent
	public static void onRenderFog(EntityViewRenderEvent.FogDensity event) {
		if(RTConfig.client.clearWater && event.getState().getMaterial() == Material.WATER) {
			event.setDensity(0);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onPotionShift(GuiScreenEvent.PotionShiftEvent event) {
		if(RTConfig.client.disablePotionShift) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onSoundPlay(PlaySoundEvent event) {
		if(event.getName().equals("entity.wither.spawn")) {
			if(RTConfig.client.disableWitherSpawnSound) {
				event.setResultSound(null);
			}

			return;
		}

		if(event.getName().equals("entity.enderdragon.death")) {
			if(RTConfig.client.disableEnderDragonDeathSound) {
				event.setResultSound(null);
			}
		}
	}

	@SubscribeEvent
	public static void onFoVUpdate(FOVUpdateEvent event) {
		if(!RTConfig.Data.get().fovChanges) {
			event.setNewfov(1.0F);
		}
	}
}
