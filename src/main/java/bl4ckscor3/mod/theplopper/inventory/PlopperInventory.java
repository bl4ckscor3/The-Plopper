package bl4ckscor3.mod.theplopper.inventory;

import bl4ckscor3.mod.theplopper.ThePlopper;
import bl4ckscor3.mod.theplopper.tileentity.TileEntityPlopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class PlopperInventory implements IInventory
{
	public static final int SLOTS = 8;
	private NonNullList<ItemStack> contents = NonNullList.<ItemStack>withSize(SLOTS, ItemStack.EMPTY);
	private PlopperItemHandler itemHandler;

	/**
	 * Sets up this inventory with the container
	 * @param te The container of this inventory
	 */
	public PlopperInventory(TileEntityPlopper te)
	{
		itemHandler = new PlopperItemHandler(te);
	}

	@Override
	public ITextComponent getName()
	{
		return new TextComponentTranslation(ThePlopper.thePlopper.getTranslationKey());
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return getName();
	}

	@Override
	public int getSizeInventory()
	{
		return SLOTS;
	}

	@Override
	public boolean isEmpty()
	{
		return contents.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index)
	{
		return contents.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		if(index == 7) //upgrade slot
			return itemHandler.extractItem(index, count, false, true);
		else return itemHandler.extractItem(index, count, false);
	}

	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		ItemStack stack = getStackInSlot(index);

		if(!stack.isEmpty())
			setInventorySlotContents(index, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		contents.set(index, stack);
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public void markDirty()
	{
		itemHandler.getTileEntity().markDirty();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return true;
	}

	@Override
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount()
	{
		return 0;
	}

	@Override
	public void clear()
	{
		for(int i = 0; i < contents.size(); i++)
		{
			contents.set(i, ItemStack.EMPTY);
		}
	}

	/**
	 * @return This inventory's item handler
	 */
	public PlopperItemHandler getItemHandler()
	{
		return itemHandler;
	}

	/*
	 * @return This inventory's contents (index 0 is slot 0, etc)
	 */
	public NonNullList<ItemStack> getContents()
	{
		return contents;
	}

	@Override
	public ITextComponent getCustomName()
	{
		return getName();
	}
}
