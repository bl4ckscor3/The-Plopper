package bl4ckscor3.mod.theplopper.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import bl4ckscor3.mod.theplopper.ThePlopper;
import bl4ckscor3.mod.theplopper.container.ContainerPlopper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiPlopper extends ContainerScreen<ContainerPlopper>
{
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("theplopper:textures/gui/container/plopper.png");

	public GuiPlopper(ContainerPlopper container, PlayerInventory playerInv, ITextComponent name)
	{
		super(container, playerInv, name);

		xSize = 200;
		ySize = 133;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		String name = I18n.format(ThePlopper.thePlopper.getTranslationKey());

		font.drawString(name, (xSize - 24) / 2 - font.getStringWidth(name) / 2, 7, 0x404040);
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
		renderBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getInstance().getTextureManager().bindTexture(GUI_TEXTURE);
		blit((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
	}
}
