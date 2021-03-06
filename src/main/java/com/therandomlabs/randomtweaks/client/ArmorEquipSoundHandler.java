package com.therandomlabs.randomtweaks.client;

import java.util.List;
import com.therandomlabs.randomtweaks.config.RTConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;

public final class ArmorEquipSoundHandler {
	private static final List<ItemStack> previousArmor = NonNullList.withSize(4, ItemStack.EMPTY);

	private ArmorEquipSoundHandler() {}

	public static void onClientTick() {
		final Minecraft mc = Minecraft.getMinecraft();

		if(!RTConfig.ArmorEquipSounds.enabled || mc.player == null) {
			return;
		}

		for(int i = 0; i < 4; i++) {
			final ItemStack previousStack = previousArmor.get(i);

			final ItemStack stack = mc.player.inventory.armorInventory.get(i);
			previousArmor.set(i, stack == ItemStack.EMPTY ? stack : stack.copy());

			if(!ItemStack.areItemsEqualIgnoreDurability(stack, previousStack)) {
				SoundEvent sound = getSound(stack);

				if(sound == null) {
					sound = getSound(previousStack);
				}

				if(sound != null) {
					mc.player.playSound(sound, 1.0F, 1.0F);
				}
			}
		}
	}

	public static SoundEvent getSound(ItemStack stack) {
		final Minecraft mc = Minecraft.getMinecraft();

		if(mc.player.ticksExisted < 20) {
			return null;
		}

		final Item item = stack.getItem();

		if(item instanceof ItemArmor) {
			return ((ItemArmor) item).getArmorMaterial().getSoundEvent();
		}

		if(item instanceof ItemElytra) {
			return RTConfig.ArmorEquipSounds.elytraSound;
		}

		if(item == Item.getItemFromBlock(Blocks.PUMPKIN)) {
			return RTConfig.ArmorEquipSounds.pumpkinSound;
		}

		if(item instanceof ItemSkull) {
			return RTConfig.ArmorEquipSounds.skullSound;
		}

		return null;
	}
}
