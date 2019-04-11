package bl4ckscor3.mod.theplopper.container;

import bl4ckscor3.mod.theplopper.ThePlopper;
import bl4ckscor3.mod.theplopper.inventory.ItemValidatorSlot;
import bl4ckscor3.mod.theplopper.tileentity.TileEntityPlopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPlopper extends Container
{
	public ContainerPlopper(InventoryPlayer playerInv, TileEntityPlopper tep)
	{
		//player inventory
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				addSlotToContainer(new Slot(playerInv, 9 + j + i * 9, 8 + j * 18, 51 + i * 18));
			}
		}

		//player hotbar
		for(int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 109));
		}

		//plopper inventory
		for(int i = 0; i < 7; i++)
		{
			addSlotToContainer(new ItemValidatorSlot(tep.getInventory(), i, 26 + i * 18, 20, stack -> false));
		}

		//upgrade slot
		addSlotToContainer(new ItemValidatorSlot(tep.getInventory(), 7, 177, 7, stack -> stack.getItem() == ThePlopper.rangeUpgrade));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) //basically the chest code, but modified a bit to e.g. include custom slots
	{
		ItemStack copy = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if(slot != null && slot.getHasStack())
		{
			ItemStack slotStack = slot.getStack();

			copy = slotStack.copy();

			if(index < 43) //any slot
			{
				if(!mergeItemStack(slotStack, 43, 44, false)) //try to merge into the upgrade slot first
					return ItemStack.EMPTY;
			}

			if(index >= 36 && index <= 43) //plopper slots
			{
				if(!mergeItemStack(slotStack, 0, 36, false))
					return ItemStack.EMPTY;
			}
			else if(index >= 27 && index <= 35) //hotbar
			{
				if(!mergeItemStack(slotStack, 0, 27, false))
					return ItemStack.EMPTY;
			}
			else if(index <= 26) //main inventory
			{
				if(!mergeItemStack(slotStack, 27, 36, false))
					return ItemStack.EMPTY;
			}

			if(slotStack.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
		}

		return copy;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}
}
