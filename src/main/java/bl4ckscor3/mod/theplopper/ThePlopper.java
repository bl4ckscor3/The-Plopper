package bl4ckscor3.mod.theplopper;

import bl4ckscor3.mod.theplopper.block.BlockPlopper;
import bl4ckscor3.mod.theplopper.gui.GuiHandler;
import bl4ckscor3.mod.theplopper.tileentity.TileEntityPlopper;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;

@Mod(ThePlopper.MOD_ID)
@EventBusSubscriber(bus=Bus.MOD)
public class ThePlopper
{
	public static final String MOD_ID = "theplopper";
	public static Block thePlopper;
	public static Item rangeUpgrade;
	public static TileEntityType<TileEntityPlopper> teTypePlopper;

	public ThePlopper()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.CONFIG_SPEC);
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.GUIFACTORY, () -> GuiHandler::getClientGuiElement);
		MinecraftForge.EVENT_BUS.addListener(this::onItemExpire);
		MinecraftForge.EVENT_BUS.addListener(this::onBlockBreak);
		//		MinecraftForge.EVENT_BUS.addListener(this::onItemToss);
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().register(thePlopper = new BlockPlopper());
	}

	@SubscribeEvent
	public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event)
	{
		teTypePlopper = TileEntityType.register(thePlopper.getRegistryName().toString(), TileEntityType.Builder.create(TileEntityPlopper::new));
	}

	@SubscribeEvent
	public static void onRegistryEventRegisterItem(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(rangeUpgrade = new Item(new Item.Properties().group(ItemGroup.REDSTONE).maxStackSize(7)).setRegistryName(new ResourceLocation(MOD_ID, "range_upgrade")));
		event.getRegistry().register(new ItemBlock(thePlopper, new Item.Properties().group(ItemGroup.REDSTONE)).setRegistryName(thePlopper.getRegistryName().toString()));
	}

	public void onItemExpire(ItemExpireEvent event)
	{
		checkForPloppers(event.getEntityItem());
	}

	/**
	 * Checks for any ploppers in increasing ranges and makes them suck up the item if applicable
	 * @param ei The item to potentially suck up
	 */
	private static void checkForPloppers(EntityItem ei)
	{
		if(ei.getEntityWorld().isRemote)
			return;

		for(TileEntityPlopper plopper : PlopperTracker.getPloppersInRange(ei.getEntityWorld(), ei.getPosition()))
		{
			//if there are multiple ploppers that could potentially pick up the item, this one will take as much as it can and let the rest be handled by others
			if(plopper.suckUp(ei, ei.getItem()))
				return;
		}
	}

	public void onBlockBreak(BlockEvent.BreakEvent event)
	{
		TileEntity te = event.getWorld().getTileEntity(event.getPos());

		if(te instanceof TileEntityPlopper && event.getWorld() instanceof World)
		{
			for(ItemStack stack : ((TileEntityPlopper)te).getInventory().getContents())
			{
				Block.spawnAsEntity((World)event.getWorld(), event.getPos(), stack);
			}
		}
	}

	/**
	 * For Testing purposes
	 */
	//TODO: Comment out on release
	//	public void onItemToss(ItemTossEvent event)
	//	{
	//		checkForPloppers(event.getEntityItem());
	//	}
}
