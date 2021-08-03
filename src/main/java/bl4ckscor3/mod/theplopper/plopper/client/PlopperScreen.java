package bl4ckscor3.mod.theplopper.plopper.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import bl4ckscor3.mod.theplopper.block.PlopperContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PlopperScreen extends ContainerScreen<PlopperContainer>
{
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("theplopper:textures/gui/container/plopper.png");

	public PlopperScreen(PlopperContainer container, PlayerInventory playerInv, ITextComponent name)
	{
		super(container, playerInv, name);

		imageWidth = 200;
		imageHeight = 133;
		inventoryLabelY = 40; //position "Inventory" correctly
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		super.render(matrix, mouseX, mouseY, partialTicks);

		renderTooltip(matrix, mouseX, mouseY);
	}

	@Override
	protected void renderBg(MatrixStack matrix, float partialTicks, int mouseX, int mouseY)
	{
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getInstance().getTextureManager().bind(GUI_TEXTURE);
		blit(matrix, (width - imageWidth) / 2, (height - imageHeight) / 2, 0, 0, imageWidth, imageHeight);
	}
}
