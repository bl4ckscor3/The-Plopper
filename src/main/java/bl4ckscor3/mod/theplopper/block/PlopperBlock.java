package bl4ckscor3.mod.theplopper.block;

import java.util.List;

import bl4ckscor3.mod.theplopper.ThePlopper;
import bl4ckscor3.mod.theplopper.tileentity.PlopperTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class PlopperBlock extends ContainerBlock implements IWaterLoggable
{
	public static final String NAME = "plopper";
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public PlopperBlock()
	{
		super(Block.Properties.create(Material.IRON, MaterialColor.STONE).hardnessAndResistance(3.0F, 8.0F).sound(SoundType.METAL));

		setRegistryName(ThePlopper.MOD_ID + ":" + NAME);
		setDefaultState(stateContainer.getBaseState().with(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx)
	{
		VoxelShape base = Block.makeCuboidShape(2, 0, 2, 14, 1, 14);
		VoxelShape hopper1 = Block.makeCuboidShape(7, 1, 7, 9, 2, 9);
		VoxelShape hopper2 = Block.makeCuboidShape(6, 2, 6, 10, 3, 10);
		VoxelShape hopper3 = Block.makeCuboidShape(5, 3, 5, 11, 4, 11);
		VoxelShape hopper4 = Block.makeCuboidShape(4, 4, 4, 12, 5, 12);

		return VoxelShapes.or(VoxelShapes.or(VoxelShapes.or(VoxelShapes.or(base, hopper1), hopper2), hopper3), hopper4); //mojang, why no varargs? :c
	}

	@Override
	public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) //onBlockActivated
	{
		if(!world.isRemote)
		{
			INamedContainerProvider containerProvider = getContainer(state, world, pos);

			if(containerProvider != null)
				player.openContainer(containerProvider);
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	public INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);

		return te instanceof PlopperTileEntity ? (INamedContainerProvider)te : null;
	}

	@Override
	public void addInformation(ItemStack stack, IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
		tooltip.add(new StringTextComponent(TextFormatting.GRAY + new TranslationTextComponent("theplopper:plopper.tooltip").getFormattedText()));
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		return getDefaultState().with(WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
	}

	@Override
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos)
	{
		if(state.get(WATERLOGGED))
			world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));

		return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public IFluidState getFluidState(BlockState state)
	{
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(WATERLOGGED);
	}

	@Override
	public boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos)
	{
		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world)
	{
		return new PlopperTileEntity();
	}
}
