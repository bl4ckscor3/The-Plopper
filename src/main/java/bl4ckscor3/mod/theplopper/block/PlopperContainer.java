package bl4ckscor3.mod.theplopper.block;

import bl4ckscor3.mod.theplopper.ThePlopper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class PlopperContainer extends AbstractContainerMenu
{
	public PlopperContainer(int windowId, Inventory playerInv, BlockEntity tile)
	{
		super(ThePlopper.cTypePlopper, windowId);

		//player inventory
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				addSlot(new Slot(playerInv, 9 + j + i * 9, 8 + j * 18, 51 + i * 18));
			}
		}

		//player hotbar
		for(int i = 0; i < 9; i++)
		{
			addSlot(new Slot(playerInv, i, 8 + i * 18, 109));
		}

		if(tile instanceof PlopperTileEntity te)
		{
			//plopper inventory
			te.getExtractOnlyInventoryHandler().ifPresent(itemHandler -> {
				for(int i = 0; i < 7; i++)
				{
					addSlot(new SlotItemHandler(itemHandler, i, 26 + i * 18, 20) {
						@Override
						public boolean mayPlace(ItemStack stack)
						{
							return false;
						}
					});
				}
			});
			//upgrade slot
			te.getUpgradeHandler().ifPresent(itemHandler -> addSlot(new SlotItemHandler(itemHandler, 0, 177, 7)));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) //basically the chest code, but modified a bit to e.g. include custom slots
	{
		ItemStack copy = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if(slot != null && slot.hasItem())
		{
			ItemStack slotStack = slot.getItem();

			copy = slotStack.copy();

			if(index != 43 && getItems().get(index).getItem() == ThePlopper.rangeUpgrade) //try to merge upgrades first
			{
				if(!moveItemStackTo(slotStack, 43, 44, false))
					return ItemStack.EMPTY;
			}

			if(index >= 36 && index <= 43) //plopper slots
			{
				if(!moveItemStackTo(slotStack, 0, 36, false))
					return ItemStack.EMPTY;
			}
			else if(index >= 27 && index <= 35) //hotbar
			{
				if(!moveItemStackTo(slotStack, 0, 27, false))
					return ItemStack.EMPTY;
			}
			else if(index <= 26) //main inventory
			{
				if(!moveItemStackTo(slotStack, 27, 36, false))
					return ItemStack.EMPTY;
			}

			if(slotStack.isEmpty())
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
		}

		return copy;
	}

	@Override
	public boolean stillValid(Player player)
	{
		return true;
	}
}
