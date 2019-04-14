package bl4ckscor3.mod.theplopper.tileentity;

import bl4ckscor3.mod.theplopper.Configuration;
import bl4ckscor3.mod.theplopper.PlopperTracker;
import bl4ckscor3.mod.theplopper.ThePlopper;
import bl4ckscor3.mod.theplopper.TickingPloppersHandler;
import bl4ckscor3.mod.theplopper.inventory.PlopperInventory;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityPlopper extends TileEntity implements ITickable
{
	private PlopperInventory inventory = new PlopperInventory(this);
	private LazyOptional<IItemHandler> inventoryHolder = LazyOptional.of(() -> inventory.getItemHandler());
	private boolean tracked = false;

	public TileEntityPlopper()
	{
		super(ThePlopper.teTypePlopper);
	}

	/**
	 * Adds the given {@link net.minecraft.item.ItemStack} to the inventory
	 * @param ei The EntityItem that gets sucked up
	 * @param stack The stack to add
	 * @return true if (part of) the stack has been sucked up, false if the stack couldn't be sucked up
	 */
	public boolean suckUp(EntityItem ei, ItemStack stack)
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
			EntityItem newEi = new EntityItem(ei.getEntityWorld(), ei.posX, ei.posY, ei.posZ, remainder);

			ei.remove();
			newEi.setAgeToCreativeDespawnTime();
			newEi.motionX = newEi.motionY = newEi.motionZ = 0.0F;
			newEi.getEntityWorld().spawnEntity(newEi);
		}
		else
			ei.remove();

		if(!world.isRemote && Configuration.CONFIG.displayParticles.get())
		{
			((WorldServer)world).spawnParticle(Particles.SMOKE, ei.posX, ei.posY + 0.25D, ei.posZ, 10, 0.0D, 0.1D, 0.0D, 0.001D);
			((WorldServer)world).spawnParticle(Particles.ENCHANT, getPos().getX() + 0.5D, getPos().getY() + 1.5D, getPos().getZ() + 0.5D, 20, 0.0D, 0.0D, 0.0D, 0.3D);
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
	public NBTTagCompound getUpdateTag()
	{
		return write(new NBTTagCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		read(pkt.getNbtCompound());
	}

	@Override
	public void read(NBTTagCompound compound)
	{
		NBTTagCompound invTag = (NBTTagCompound)compound.get("PlopperInventory");

		for(int i = 0; i < inventory.getContents().size(); i++)
		{
			if(invTag != null && invTag.contains("Slot" + i))
				inventory.setInventorySlotContents(i, ItemStack.read((NBTTagCompound)invTag.get("Slot" + i)));
		}

		super.read(compound);
	}

	@Override
	public NBTTagCompound write(NBTTagCompound compound)
	{
		NBTTagCompound invTag = new NBTTagCompound();

		for(int i = 0; i < inventory.getContents().size(); i++)
		{
			invTag.put("Slot" + i, inventory.getStackInSlot(i).write(new NBTTagCompound()));
		}

		compound.put("PlopperInventory", invTag);
		return super.write(compound);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, EnumFacing side)
	{
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (side == EnumFacing.DOWN || Configuration.CONFIG.bypassOutputSide.get()))
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
}
