package bl4ckscor3.mod.theplopper.block;

import java.util.List;

import bl4ckscor3.mod.theplopper.ThePlopper;
import bl4ckscor3.mod.theplopper.tileentity.TileEntityPlopper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPlopper extends BlockContainer
{
	public static final String NAME = "plopper";

	public BlockPlopper()
	{
		super(Material.IRON, MapColor.STONE);

		setRegistryName(ThePlopper.MOD_ID + ":" + NAME);
		setTranslationKey(ThePlopper.MOD_ID + ":" + NAME);
		setHardness(3.0F);
		setResistance(8.0F);
		setSoundType(SoundType.METAL);
		setCreativeTab(CreativeTabs.REDSTONE);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		float px = 1.0F / 16.0F;

		return new AxisAlignedBB(px * 2.0F, 0, px * 2.0F, px * 14.0F, 5 * px, px * 14.0F);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!worldIn.isRemote)
			playerIn.openGui(ThePlopper.MOD_ID, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		TileEntity te = worldIn.getTileEntity(pos);

		if(te instanceof TileEntityPlopper)
		{
			for(ItemStack stack : ((TileEntityPlopper)te).getInventory().getContents())
			{
				Block.spawnAsEntity(worldIn, pos, stack);
			}
		}
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		tooltip.add(I18n.translateToLocal("theplopper:plopper.tooltip"));
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
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
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityPlopper();
	}
}
