package bl4ckscor3.mod.theplopper.tileentity;

import bl4ckscor3.mod.theplopper.Configuration;
import bl4ckscor3.mod.theplopper.PlopperTracker;
import bl4ckscor3.mod.theplopper.ThePlopper;
import bl4ckscor3.mod.theplopper.TickingPloppersHandler;
import bl4ckscor3.mod.theplopper.container.ContainerPlopper;
import bl4ckscor3.mod.theplopper.inventory.PlopperInventory;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityPlopper extends TileEntity implements ITickableTileEntity, INamedContainerProvider
{
	private PlopperInventory inventory = new PlopperInventory(this);
	private final LazyOptional<IItemHandler> inventoryHolder = LazyOptional.of(() -> inventory.getItemHandler());
	private boolean tracked = false;

	public TileEntityPlopper()
	{
		super(ThePlopper.teTypePlopper);
	}

	/**
	 * Adds the given {@link net.minecraft.item.ItemStack} to the inventory
	 * @param ei The ItemEntity that gets sucked up
	 * @param stack The stack to add
	 * @return true if (part of) the stack has been sucked up, false if the stack couldn't be sucked up
	 */
	public boolean suckUp(ItemEntity ei, ItemStack stack)
	{
		ItemStack remainder = stack;

		for(int i = 0; i < inventory.getContents().size() - 1; i++) //-1 so the last slot is not checked (the upgrade slot)
		{
			remainder = inventory.getItemHandler().insertItem(i, remainder, false);

			if(remainder.isEmpty())
				break;
		}

		if(remainder.equals(stack))
		{
			world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
			return false;
		}

		if(!remainder.isEmpty())
		{
			ItemEntity newEi = new ItemEntity(ei.getEntityWorld(), ei.func_226277_ct_(), ei.func_226278_cu_(), ei.func_226281_cx_(), remainder);

			ei.remove();
			newEi.setMotion(0.0D, 0.0D, 0.0D);
			newEi.getEntityWorld().addEntity(newEi);
		}
		else
			ei.remove();

		if(!world.isRemote && Configuration.CONFIG.displayParticles.get())
		{
			((ServerWorld)world).spawnParticle(ParticleTypes.SMOKE, ei.func_226277_ct_(), ei.func_226278_cu_() + 0.25D, ei.func_226281_cx_(), 10, 0.0D, 0.1D, 0.0D, 0.001D);
			((ServerWorld)world).spawnParticle(ParticleTypes.ENCHANT, getPos().getX() + 0.5D, getPos().getY() + 1.5D, getPos().getZ() + 0.5D, 20, 0.0D, 0.0D, 0.0D, 0.3D);
		}

		if(Configuration.CONFIG.playSound.get())
			ei.getEntityWorld().playSound(null, ei.getPosition(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.chicken.egg")), SoundCategory.NEUTRAL, 1.0F, 1.0F);

		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
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
	public void remove()
	{
		super.remove();

		PlopperTracker.stopTracking(this);
		tracked = false;
	}

	@Override
	public CompoundNBT getUpdateTag()
	{
		return write(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket()
	{
		return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
	{
		read(pkt.getNbtCompound());
	}

	@Override
	public void read(CompoundNBT compound)
	{
		CompoundNBT invTag = (CompoundNBT)compound.get("PlopperInventory");

		for(int i = 0; i < inventory.getContents().size(); i++)
		{
			if(invTag != null && invTag.contains("Slot" + i))
				inventory.setInventorySlotContents(i, ItemStack.read((CompoundNBT)invTag.get("Slot" + i)));
		}

		super.read(compound);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound)
	{
		CompoundNBT invTag = new CompoundNBT();

		for(int i = 0; i < inventory.getContents().size(); i++)
		{
			invTag.put("Slot" + i, inventory.getStackInSlot(i).write(new CompoundNBT()));
		}

		compound.put("PlopperInventory", invTag);
		return super.write(compound);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (side == Direction.DOWN || Configuration.CONFIG.bypassOutputSide.get()))
			return inventoryHolder.cast();
		else return super.getCapability(cap, side);
	}

	/**
	 * @return This tile's inventory
	 */
	public PlopperInventory getInventory()
	{
		return inventory;
	}

	/**
	 * @return The range this plopper will pick up items in
	 */
	public AxisAlignedBB getRange()
	{
		//slot 7 is the upgrade slot
		int range = 2 + inventory.getStackInSlot(7).getCount() * 2;
		int x = getPos().getX();
		int y = getPos().getY();
		int z = getPos().getZ();
		return new AxisAlignedBB(x - range, y - range, z - range, x + range, y + range, z + range);
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player)
	{
		return new ContainerPlopper(windowId, playerInv, inventory);
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return new TranslationTextComponent(ThePlopper.thePlopper.getTranslationKey());
	}
}
