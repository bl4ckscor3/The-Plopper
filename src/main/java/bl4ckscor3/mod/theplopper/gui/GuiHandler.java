package bl4ckscor3.mod.theplopper.gui;

import bl4ckscor3.mod.theplopper.container.ContainerPlopper;
import bl4ckscor3.mod.theplopper.tileentity.TileEntityPlopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(world.getTileEntity(new BlockPos(x, y, z)) instanceof TileEntityPlopper)
			return new ContainerPlopper(player.inventory, (TileEntityPlopper)world.getTileEntity(new BlockPos(x, y, z)));
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(world.getTileEntity(new BlockPos(x, y, z)) instanceof TileEntityPlopper)
			return new GuiPlopper(player.inventory, (TileEntityPlopper)world.getTileEntity(new BlockPos(x, y, z)));
		return null;
	}
}
