package bl4ckscor3.mod.theplopper.block;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ExtractOnlyItemStackHandler extends ItemStackHandler {
	public ExtractOnlyItemStackHandler(NonNullList<ItemStack> stacks) {
		super(stacks);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return ItemStack.EMPTY;
	}
}
