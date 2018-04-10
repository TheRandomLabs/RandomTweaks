package com.therandomlabs.randomtweaks.server;

import com.therandomlabs.randomtweaks.util.Compat;
import com.therandomlabs.randomtweaks.util.Utils;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandGive;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;

public class CommandRTGive extends CommandGive {
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
			throws CommandException {
		if(args.length < 2) {
			throw new WrongUsageException(getUsage(sender));
		}

		final EntityPlayer player = getPlayer(server, sender, args[0]);

		Item item = null;
		try {
			item = Item.getItemById(parseInt(args[1], 1));
		} catch(NumberInvalidException ex) {}

		if(item == null) {
			item = getItemByText(sender, args[1]);
		}

		final int amount = args.length > 2 ? parseInt(args[2], 1) : 1;
		final int meta = args.length > 3 ? parseInt(args[3]) : 0;
		final ItemStack stack = new ItemStack(item, amount, meta);

		if(args.length > 4) {
			final String tag = Compat.buildString(args, 4);
			try {
				stack.setTagCompound(JsonToNBT.getTagFromJson(tag));
			} catch(NBTException ex) {
				ex.printStackTrace();
				throw new CommandException("commands.give.tagError", ex.getMessage());
			}
		}

		final boolean added = player.inventory.addItemStackToInventory(stack);
		if(added) {
			player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ,
					SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
					((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) *
							2.0F);

			Compat.detectAndSendChanges(player.inventoryContainer);

			if(Compat.isEmpty(stack)) {
				try {
					Compat.setStackSize(stack, 1);
				} catch(Exception ex) {
					ex.printStackTrace();
					throw new CommandException(Utils.localize("commands.rtgive.failure",
							ex.getClass().getName() + ": " + ex.getMessage()));
				}

				sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, amount);

				final EntityItem droppedItem = player.dropItem(stack, false);

				if(droppedItem != null) {
					droppedItem.makeFakeItem();
				}
			} else {
				try {
					sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS,
							amount - Compat.getStackSize(stack));
				} catch(Exception ex) {
					ex.printStackTrace();
					throw new CommandException(Utils.localize("commands.rtgive.failure",
							ex.getClass().getName() + ": " + ex.getMessage()));
				}

				final EntityItem droppedItem = player.dropItem(stack, false);

				if(droppedItem != null) {
					droppedItem.setNoPickupDelay();
					droppedItem.setOwner(player.getName());
				}
			}

			notifyCommandListener(sender, this, "commands.give.success", stack.getTextComponent(),
					amount, player.getName());
		}
	}
}
