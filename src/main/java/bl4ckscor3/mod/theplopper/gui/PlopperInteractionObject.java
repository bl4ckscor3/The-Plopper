package bl4ckscor3.mod.theplopper.gui;

import bl4ckscor3.mod.theplopper.container.ContainerPlopper;
import bl4ckscor3.mod.theplopper.tileentity.TileEntityPlopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

public class PlopperInteractionObject implements IInteractionObject
{
	private final ResourceLocation id;
	private final World world;
	private final BlockPos pos;

	public PlopperInteractionObject(ResourceLocation id, World world, BlockPos pos)
	{
		this.id = id;
		this.world = world;
		this.pos = pos;
	}

	@Override
	public ITextComponent getName()
	{
		return new TextComponentString(id.toString());
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public ITextComponent getCustomName()
	{
		return null;
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player)
	{
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TileEntityPlopper)
			return new ContainerPlopper(playerInventory, (TileEntityPlopper)te);
		else
			return null;
	}

	@Override
	public String getGuiID()
	{
		return id.toString();
	}
}
