package bl4ckscor3.mod.theplopper.block;

import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

public class PlopperItem extends BlockItem {
	private static final Style GRAY_STYLE = Style.EMPTY.applyFormat(ChatFormatting.GRAY);

	public PlopperItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
		tooltipAdder.accept(Component.translatable("theplopper:plopper.tooltip").setStyle(GRAY_STYLE));
	}
}
