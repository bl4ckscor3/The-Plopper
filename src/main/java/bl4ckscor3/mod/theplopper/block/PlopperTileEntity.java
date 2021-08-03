package bl4ckscor3.mod.theplopper.block;

import bl4ckscor3.mod.theplopper.Configuration;
import bl4ckscor3.mod.theplopper.ThePlopper;
import bl4ckscor3.mod.theplopper.tracking.PlopperTracker;
import bl4ckscor3.mod.theplopper.tracking.TickingPloppersHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class PlopperTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider
{
	public static final int SLOTS = 8;
	private NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(SLOTS, ItemStack.EMPTY);
	private LazyOptional<IItemHandler> inventoryHandler;
	private boolean tracked = false;

	public PlopperTileEntity()
	{
		super(ThePlopper.teTypePlopper);
	}

	/**
	 * Adds the given {@link net.minecraft.item.ItemStack} to the inventory
	 * @param ie The ItemEntity that gets sucked up
	 * @param stack The stack to add
	 * @return true if (part of) the stack has been sucked up, false if the stack couldn't be sucked up
	 */
	public boolean suckUp(ItemEntity ie, ItemStack stack)
	{
		ItemStack remainder = stack;

		for(int i = 0; i < inventory.size() - 1; i++) //-1 so the last slot is not checked (the upgrade slot)
		{
			IItemHandler itemHandler = getInventoryHandler().orElse(null);

			if(itemHandler != null)
				remainder = ((PlopperItemHandler)itemHandler).insertItem(i, remainder, false);
			else
				return false;

			if(remainder.isEmpty())
				break;
		}

		if(remainder.equals(stack))
		{
			level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
			return false;
		}

		if(!remainder.isEmpty())
		{
			ItemEntity newIe = new ItemEntity(ie.getCommandSenderWorld(), ie.getX(), ie.getY(), ie.getZ(), remainder);

			ie.remove();
			newIe.setDeltaMovement(0.0D, 0.0D, 0.0D);
			newIe.getCommandSenderWorld().addFreshEntity(newIe);
		}
		else
			ie.remove();

		if(!level.isClientSide && Configuration.CONFIG.displayParticles.get())
		{
			((ServerWorld)level).sendParticles(ParticleTypes.SMOKE, ie.getX(), ie.getY() + 0.25D, ie.getZ(), 10, 0.0D, 0.1D, 0.0D, 0.001D);
			((ServerWorld)level).sendParticles(ParticleTypes.ENCHANT, getBlockPos().getX() + 0.5D, getBlockPos().getY() + 1.5D, getBlockPos().getZ() + 0.5D, 20, 0.0D, 0.0D, 0.0D, 0.3D);
		}

		if(Configuration.CONFIG.playSound.get())
			ie.getCommandSenderWorld().playSound(null, ie.blockPosition(), SoundEvents.CHICKEN_EGG, SoundCategory.NEUTRAL, 1.0F, 1.0F);

		level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
		return true;
	}

	@Override
	public void tick()
	{
		if(!tracked)
		{
			PlopperTracker.track(this);
			TickingPloppersHandler.stopTicking(this);
			tracked = true;
		}
	}

	@Override
	public void setRemoved()
	{
		super.setRemoved();

		PlopperTracker.stopTracking(this);
		tracked = false;
	}

	@Override
	public CompoundNBT getUpdateTag()
	{
		return save(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket()
	{
		return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
	{
		load(getBlockState(), pkt.getTag());
	}

	@Override
	public void load(BlockState state, CompoundNBT compound)
	{
		CompoundNBT invTag = (CompoundNBT)compound.get("PlopperInventory");

		for(int i = 0; i < inventory.size(); i++)
		{
			if(invTag != null && invTag.contains("Slot" + i))
				inventory.set(i, ItemStack.of((CompoundNBT)invTag.get("Slot" + i)));
		}

		super.load(state, compound);
	}

	@Override
	public CompoundNBT save(CompoundNBT compound)
	{
		CompoundNBT invTag = new CompoundNBT();

		for(int i = 0; i < inventory.size(); i++)
		{
			invTag.put("Slot" + i, inventory.get(i).save(new CompoundNBT()));
		}

		compound.put("PlopperInventory", invTag);
		return super.save(compound);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (side == Direction.DOWN || Configuration.CONFIG.bypassOutputSide.get()))
			return inventoryHandler.cast();
		else return super.getCapability(cap, side);
	}

	/**
	 * @return The range this plopper will pick up items in
	 */
	public AxisAlignedBB getRange()
	{
		//slot 7 is the upgrade slot
		int range = 2 + inventory.get(7).getCount() * 2;
		int x = getBlockPos().getX();
		int y = getBlockPos().getY();
		int z = getBlockPos().getZ();
		return new AxisAlignedBB(x - range, y - range, z - range, x + range, y + range, z + range);
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player)
	{
		return new PlopperContainer(windowId, playerInv, this);
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return new TranslationTextComponent(ThePlopper.thePlopper.getDescriptionId());
	}

	public NonNullList<ItemStack> getInventory()
	{
		return inventory;
	}

	public LazyOptional<IItemHandler> getInventoryHandler()
	{
		if(inventoryHandler == null)
			inventoryHandler = LazyOptional.of(() -> new PlopperItemHandler(PlopperTileEntity.this));

		return inventoryHandler;
	}
}
