package bl4ckscor3.mod.theplopper.block;

import java.util.List;

import bl4ckscor3.mod.theplopper.ThePlopper;
import bl4ckscor3.mod.theplopper.gui.GuiHandler;
import bl4ckscor3.mod.theplopper.gui.PlopperInteractionObject;
import bl4ckscor3.mod.theplopper.tileentity.TileEntityPlopper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockPlopper extends BlockContainer
{
	public static final String NAME = "plopper";

	public BlockPlopper()
	{
		super(Block.Properties.create(Material.IRON, MaterialColor.STONE).hardnessAndResistance(3.0F, 8.0F).sound(SoundType.METAL));

		setRegistryName(ThePlopper.MOD_ID + ":" + NAME);
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader source, BlockPos pos)
	{
		VoxelShape base = Block.makeCuboidShape(2, 0, 2, 14, 1, 14);
		VoxelShape hopper1 = Block.makeCuboidShape(7, 1, 7, 9, 2, 9);
		VoxelShape hopper2 = Block.makeCuboidShape(6, 2, 6, 10, 3, 10);
		VoxelShape hopper3 = Block.makeCuboidShape(5, 3, 5, 11, 4, 11);
		VoxelShape hopper4 = Block.makeCuboidShape(4, 4, 4, 12, 5, 12);

		return VoxelShapes.or(VoxelShapes.or(VoxelShapes.or(VoxelShapes.or(base, hopper1), hopper2), hopper3), hopper4); //mojang, why no varargs? :c
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(!world.isRemote)
			NetworkHooks.openGui((EntityPlayerMP)player, new PlopperInteractionObject(GuiHandler.PLOPPER_GUI_ID, world, pos), data -> data.writeBlockPos(pos));
		return true;
	}

	@Override
	public void addInformation(ItemStack stack, IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
		tooltip.add(new TextComponentString(TextFormatting.GRAY + new TextComponentTranslation("theplopper:plopper.tooltip").getFormattedText()));
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockReader world, BlockPos pos)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world)
	{
		return new TileEntityPlopper();
	}
}
