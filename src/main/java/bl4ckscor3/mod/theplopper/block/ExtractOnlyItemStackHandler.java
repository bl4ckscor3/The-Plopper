package bl4ckscor3.mod.theplopper.block;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public class ExtractOnlyItemStackHandler extends ItemStackHandler
{
	public ExtractOnlyItemStackHandler(NonNullList<ItemStack> stacks)
	{
		super(stacks);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		return ItemStack.EMPTY;
	}
}
