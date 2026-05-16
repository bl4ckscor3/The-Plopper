package bl4ckscor3.mod.theplopper.block;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class ExtractOnlyResourceHandler extends ItemStacksResourceHandler {
	public ExtractOnlyResourceHandler(NonNullList<ItemStack> stacks) {
		super(stacks);
	}

	@Override
	public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
		return 0;
	}

	@Override
	public int insert(ItemResource resource, int amount, TransactionContext transaction) {
		return 0;
	}
}
