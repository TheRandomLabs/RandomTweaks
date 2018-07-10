package com.therandomlabs.randomtweaks.common;

import com.therandomlabs.randomtweaks.RTConfig;
import com.therandomlabs.randomtweaks.RandomTweaks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RandomTweaks.MODID)
public final class PlayerHeadDropHandler {
	@SubscribeEvent
	public static void onPlayerDrops(PlayerDropsEvent event) {
		if(!RTConfig.playerHeadDrops.enabled || RandomTweaks.PLAYERS_DROP_HEADS_LOADED ||
				RandomTweaks.HEADCRUMBS_LOADED) {
			return;
		}

		final EntityPlayer player = event.getEntityPlayer();

		if(player.getEntityWorld().isRemote) {
			return;
		}

		final Entity source = event.getSource().getTrueSource();

		if(source instanceof EntityCreeper) {
			final EntityCreeper creeper = (EntityCreeper) source;

			if(creeper.getPowered() && creeper.ableToCauseSkullDrop()) {
				if(dropSkull(player, RTConfig.playerHeadDrops.chanceWhenKilledByChargedCreeper)) {
					creeper.incrementDroppedSkulls();
					return;
				}
			}
		}

		if(source != player && source instanceof EntityPlayer) {
			dropSkull(player, RTConfig.playerHeadDrops.chanceWhenKilledByPlayer);
			return;
		}

		dropSkull(player, RTConfig.playerHeadDrops.normalChance);
	}

	public static boolean dropSkull(EntityPlayer player, double chance) {
		if(chance != 1.0 && player.getEntityWorld().rand.nextDouble() >= chance) {
			return false;
		}

		final ItemStack stack = new ItemStack(Items.SKULL, 1, 3);
		stack.setTagCompound(new NBTTagCompound());

		final NBTTagCompound skullOwner = new NBTTagCompound();
		NBTUtil.writeGameProfile(skullOwner, player.getGameProfile());

		stack.getTagCompound().setTag("SkullOwner", skullOwner);

		player.dropItem(stack, true, false);

		return true;
	}
}
