package bl4ckscor3.mod.theplopper.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import bl4ckscor3.mod.theplopper.container.PlopperContainer;
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

		xSize = 200;
		ySize = 133;
		field_238745_s_ = 40; //position "Inventory" correctly
	}

	@Override
	protected void func_230451_b_(MatrixStack matrix, int mouseX, int mouseY)
	{
		super.func_230451_b_(matrix, mouseX, mouseY);
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		super.render(matrix, mouseX, mouseY, partialTicks);

		func_230459_a_(matrix, mouseX, mouseY);
	}

	@Override
	protected void func_230450_a_(MatrixStack matrix, float partialTicks, int mouseX, int mouseY)
	{
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getInstance().getTextureManager().bindTexture(GUI_TEXTURE);
		blit(matrix, (width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
	}
}
