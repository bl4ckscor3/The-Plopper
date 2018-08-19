package bl4ckscor3.mod.theplopper.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class OutputSlot extends Slot
{
	/**
	 * A slot that only allows items to be pulled out, not placed inside
	 */
	public OutputSlot(IInventory inventoryIn, int index, int xPosition, int yPosition)
	{
		super(inventoryIn, index, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return false;
	}
}
