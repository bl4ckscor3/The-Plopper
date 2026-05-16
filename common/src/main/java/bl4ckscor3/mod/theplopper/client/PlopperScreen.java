package bl4ckscor3.mod.theplopper.client;

import bl4ckscor3.mod.theplopper.ThePlopper;
import bl4ckscor3.mod.theplopper.block.PlopperMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class PlopperScreen extends AbstractContainerScreen<PlopperMenu> {
	private static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath(ThePlopper.MODID, "textures/gui/container/plopper.png");

	public PlopperScreen(PlopperMenu container, Inventory playerInv, Component name) {
		super(container, playerInv, name, 200, 133);

		inventoryLabelY = 40; //position "Inventory" correctly
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
		extractTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.extractBackground(guiGraphics, mouseX, mouseY, partialTicks);
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, (width - imageWidth) / 2, (height - imageHeight) / 2, 0, 0, imageWidth, imageHeight, 256, 256);
	}
}
