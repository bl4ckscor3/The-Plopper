package bl4ckscor3.mod.theplopper.tileentity;

import bl4ckscor3.mod.theplopper.Configuration;
import bl4ckscor3.mod.theplopper.inventory.PlopperInventory;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityPlopper extends TileEntity
{
	private PlopperInventory inventory = new PlopperInventory(this);
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

			ei.setDead();
			newEi.setAgeToCreativeDespawnTime();
			newEi.motionX = newEi.motionY = newEi.motionZ = 0.0F;
			newEi.getEntityWorld().spawnEntity(newEi);
		}
		else
			ei.setDead();

		if(!world.isRemote && Configuration.displayParticles)
		{
			((WorldServer)world).spawnParticle(EnumParticleTypes.SMOKE_NORMAL, ei.posX, ei.posY + 0.25D, ei.posZ, 10, 0.0D, 0.1D, 0.0D, 0.001D);
			((WorldServer)world).spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, getPos().getX() + 0.5D, getPos().getY() + 1.5D, getPos().getZ() + 0.5D, 20, 0.0D, 0.0D, 0.0D, 0.3D);
		}

		if(Configuration.playSound)
			ei.getEntityWorld().playSound(null, ei.getPosition(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.chicken.egg")), SoundCategory.NEUTRAL, 1.0F, 1.0F);

		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		NBTTagCompound invTag = (NBTTagCompound)compound.getTag("PlopperInventory");

		for(int i = 0; i < inventory.getContents().size(); i++)
		{
			if(invTag != null && invTag.hasKey("Slot" + i))
				inventory.setInventorySlotContents(i, new ItemStack((NBTTagCompound)invTag.getTag("Slot" + i)));
		}

		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		NBTTagCompound invTag = new NBTTagCompound();

		for(int i = 0; i < inventory.getContents().size(); i++)
		{
			invTag.setTag("Slot" + i, inventory.getStackInSlot(i).writeToNBT(new NBTTagCompound()));
		}

		compound.setTag("PlopperInventory", invTag);
		return super.writeToNBT(compound);
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing facing)
	{
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == EnumFacing.DOWN || Configuration.bypassOutputSide))
			return true;
		return super.hasCapability(cap, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing facing)
	{
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == EnumFacing.DOWN || Configuration.bypassOutputSide))
			return (T) inventory.getItemHandler();
		return super.getCapability(cap, facing);
	}

	/**
	 * @return This tile's inventory
	 */
	public PlopperInventory getInventory()
	{
		return inventory;
	}
}
