package bl4ckscor3.mod.theplopper.gui;

import bl4ckscor3.mod.theplopper.ThePlopper;
import bl4ckscor3.mod.theplopper.tileentity.TileEntityPlopper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.FMLPlayMessages.OpenContainer;

public class GuiHandler
{
	public static final ResourceLocation PLOPPER_GUI_ID = new ResourceLocation(ThePlopper.MOD_ID, "plopper");

	public static GuiScreen getClientGuiElement(OpenContainer message)
	{
		EntityPlayerSP player = Minecraft.getInstance().player;

		if(message.getId().equals(PLOPPER_GUI_ID))
		{
			TileEntity te = Minecraft.getInstance().world.getTileEntity(message.getAdditionalData().readBlockPos());

			if(te instanceof TileEntityPlopper)
				return new GuiPlopper(player.inventory, (TileEntityPlopper)te);
		}

		return null;
	}
}
