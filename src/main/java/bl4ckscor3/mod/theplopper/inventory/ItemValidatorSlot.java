package bl4ckscor3.mod.theplopper.inventory;

import java.util.function.Function;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

/**
 * Slot to handle the validity of an item more easily
 */
public class ItemValidatorSlot extends Slot
{
	private final Function<ItemStack,Boolean> itemValidator;

	/**
	 * @param itemValidator A function to return whether an item is valid for this slot
	 */
	public ItemValidatorSlot(IInventory inventory, int index, int xPosition, int yPosition, Function<ItemStack,Boolean> itemValidator)
	{
		super(inventory, index, xPosition, yPosition);

		this.itemValidator = itemValidator;
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return itemValidator.apply(stack);
	}
}
