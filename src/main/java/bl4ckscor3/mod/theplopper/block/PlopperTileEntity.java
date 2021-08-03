package bl4ckscor3.mod.theplopper.block;

import bl4ckscor3.mod.theplopper.Configuration;
import bl4ckscor3.mod.theplopper.ThePlopper;
import bl4ckscor3.mod.theplopper.tracking.PlopperTracker;
import bl4ckscor3.mod.theplopper.tracking.TickingPloppersHandler;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class PlopperTileEntity extends BlockEntity implements TickableBlockEntity, MenuProvider
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
			((ServerLevel)level).sendParticles(ParticleTypes.SMOKE, ie.getX(), ie.getY() + 0.25D, ie.getZ(), 10, 0.0D, 0.1D, 0.0D, 0.001D);
			((ServerLevel)level).sendParticles(ParticleTypes.ENCHANT, getBlockPos().getX() + 0.5D, getBlockPos().getY() + 1.5D, getBlockPos().getZ() + 0.5D, 20, 0.0D, 0.0D, 0.0D, 0.3D);
		}

		if(Configuration.CONFIG.playSound.get())
			ie.getCommandSenderWorld().playSound(null, ie.blockPosition(), SoundEvents.CHICKEN_EGG, SoundSource.NEUTRAL, 1.0F, 1.0F);

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
	public CompoundTag getUpdateTag()
	{
		return save(new CompoundTag());
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
		return new ClientboundBlockEntityDataPacket(worldPosition, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt)
	{
		load(getBlockState(), pkt.getTag());
	}

	@Override
	public void load(BlockState state, CompoundTag compound)
	{
		CompoundTag invTag = (CompoundTag)compound.get("PlopperInventory");

		for(int i = 0; i < inventory.size(); i++)
		{
			if(invTag != null && invTag.contains("Slot" + i))
				inventory.set(i, ItemStack.of((CompoundTag)invTag.get("Slot" + i)));
		}

		super.load(state, compound);
	}

	@Override
	public CompoundTag save(CompoundTag compound)
	{
		CompoundTag invTag = new CompoundTag();

		for(int i = 0; i < inventory.size(); i++)
		{
			invTag.put("Slot" + i, inventory.get(i).save(new CompoundTag()));
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
	public AABB getRange()
	{
		//slot 7 is the upgrade slot
		int range = 2 + inventory.get(7).getCount() * 2;
		int x = getBlockPos().getX();
		int y = getBlockPos().getY();
		int z = getBlockPos().getZ();
		return new AABB(x - range, y - range, z - range, x + range, y + range, z + range);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player)
	{
		return new PlopperContainer(windowId, playerInv, this);
	}

	@Override
	public Component getDisplayName()
	{
		return new TranslatableComponent(ThePlopper.thePlopper.getDescriptionId());
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
