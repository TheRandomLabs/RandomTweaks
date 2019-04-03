package com.therandomlabs.randomtweaks.common.command;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import com.therandomlabs.randomtweaks.RandomTweaks;
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
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.oredict.OreDictionary;

public class CommandRTGive extends CommandGive {
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
			throws CommandException {
		if(args.length < 2) {
			throw new WrongUsageException(getUsage(sender));
		}

		final EntityPlayer player = getPlayer(server, sender, args[0]);

		Item item = null;
		int meta = args.length > 3 ? parseInt(args[3]) : 0;

		try {
			item = getItemByText(sender, args[1]);
		} catch(NumberInvalidException ex) {
			if(args[1].startsWith("ore:")) {
				final List<ItemStack> ores = OreDictionary.getOres(args[1].substring(4));

				if(!ores.isEmpty()) {
					final ItemStack ore = ores.get(0);

					item = ore.getItem();
					meta = ore.getItemDamage();
				}
			} else {
				try {
					item = Item.getItemById(parseInt(args[1], 1));
				} catch(NumberInvalidException ignored) {}
			}
		}

		if(item == null) {
			throw new NumberInvalidException("commands.give.item.notFound", args[1]);
		}

		final int amount = args.length > 2 ? parseInt(args[2], 1) : 1;

		final ItemStack stack = new ItemStack(item, amount, meta);

		if(args.length > 4) {
			final String tag = buildString(args, 4);

			try {
				stack.setTagCompound(JsonToNBT.getTagFromJson(tag));
			} catch(NBTException ex) {
				RandomTweaks.LOGGER.error("Failed to parse data tag", ex);
				throw new CommandException("commands.give.tagError", ex.getMessage());
			}
		}

		final boolean added = player.inventory.addItemStackToInventory(stack);

		if(added) {
			final Random rng = player.getRNG();
			final float random1 = rng.nextFloat();
			final float random2 = rng.nextFloat();

			player.getEntityWorld().playSound(
					null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP,
					SoundCategory.PLAYERS, 0.2F, ((random1 - random2) * 0.7F + 1.0F) * 2.0F
			);

			player.inventoryContainer.detectAndSendChanges();

			if(stack.stackSize <= 0) {
				stack.stackSize = 1;
				sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, amount);

				final EntityItem droppedItem = player.dropItem(stack, false);

				if(droppedItem != null) {
					droppedItem.makeFakeItem();
				}
			} else {
				sender.setCommandStat(
						CommandResultStats.Type.AFFECTED_ITEMS,
						amount - stack.stackSize
				);

				final EntityItem droppedItem = player.dropItem(stack, false);

				if(droppedItem != null) {
					droppedItem.setNoPickupDelay();
					droppedItem.setOwner(player.getName());
				}
			}

			notifyCommandListener(
					sender, this, "commands.give.success",
					stack.getTextComponent(), amount, player.getName()
			);
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
			String[] args, BlockPos targetPos) {
		final List<String> tabCompletions =
				super.getTabCompletions(server, sender, args, targetPos);

		if(args.length == 2) {
			final String[] names = OreDictionary.getOreNames();

			for(int i = 0; i < names.length; i++) {
				names[i] = "ore:" + names[i];
			}

			tabCompletions.addAll(getListOfStringsMatchingLastWord(args, Arrays.asList(names)));
		}

		return tabCompletions;
	}
}
