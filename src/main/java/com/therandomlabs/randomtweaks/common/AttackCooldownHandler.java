package com.therandomlabs.randomtweaks.common;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = RandomTweaks.MODID)
public final class AttackCooldownHandler {
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if(event.getEntity() instanceof EntityPlayer) {
			((EntityPlayer) event.getEntity()).
					getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).
					setBaseValue(RTConfig.general.attackSpeed);
		}
	}
}
