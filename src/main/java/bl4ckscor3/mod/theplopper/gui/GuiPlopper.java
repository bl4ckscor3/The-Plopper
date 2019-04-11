package bl4ckscor3.mod.theplopper.gui;

import bl4ckscor3.mod.theplopper.ThePlopper;
import bl4ckscor3.mod.theplopper.container.ContainerPlopper;
import bl4ckscor3.mod.theplopper.tileentity.TileEntityPlopper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiPlopper extends GuiContainer
{
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("theplopper:textures/gui/container/plopper.png");

	public GuiPlopper(InventoryPlayer playerInv, TileEntityPlopper te)
	{
		super(new ContainerPlopper(playerInv, te));

		xSize = 200;
		ySize = 133;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		String name = I18n.format(ThePlopper.thePlopper.getTranslationKey());

		fontRenderer.drawString(name, (xSize - 24) / 2 - fontRenderer.getStringWidth(name) / 2, 7, 0x404040);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		super.render(mouseX, mouseY, partialTicks);

		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		drawDefaultBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_TEXTURE);
		drawTexturedModalRect((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
	}
}
