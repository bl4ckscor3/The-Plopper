package bl4ckscor3.mod.theplopper.tileentity;

import bl4ckscor3.mod.theplopper.Configuration;
import bl4ckscor3.mod.theplopper.ThePlopper;
import bl4ckscor3.mod.theplopper.inventory.PlopperInventory;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityPlopper extends TileEntity
{
	private PlopperInventory inventory = new PlopperInventory(this);
	private LazyOptional<IItemHandler> inventoryHolder = LazyOptional.of(() -> inventory.getItemHandler());

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

		for(int i = 0; i < inventory.getContents().size(); i++)
		{
			remainder = inventory.getItemHandler().insertItem(i, remainder, false);

			if(remainder.isEmpty())
				break;
		}

		if(remainder.equals(stack))
			return false;

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

		return true;
	}

	@Override
	public void read(NBTTagCompound compound)
	{
		NBTTagCompound invTag = (NBTTagCompound)compound.get("PlopperInventory");

		for(int i = 0; i < inventory.getContents().size(); i++)
		{
			if(invTag.contains("Slot" + i))
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
}
