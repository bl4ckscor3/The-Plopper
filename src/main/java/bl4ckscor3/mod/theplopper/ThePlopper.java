package bl4ckscor3.mod.theplopper;

import bl4ckscor3.mod.theplopper.block.PlopperBlock;
import bl4ckscor3.mod.theplopper.container.PlopperContainer;
import bl4ckscor3.mod.theplopper.inventory.PlopperInventory;
import bl4ckscor3.mod.theplopper.tileentity.PlopperTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
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
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ObjectHolder;

@Mod(ThePlopper.MOD_ID)
@EventBusSubscriber(bus=Bus.MOD)
public class ThePlopper
{
	public static final String MOD_ID = "theplopper";
	@ObjectHolder(MOD_ID + ":plopper")
	public static Block thePlopper;
	@ObjectHolder(MOD_ID + ":range_upgrade")
	public static Item rangeUpgrade;
	@ObjectHolder(MOD_ID + ":plopper")
	public static TileEntityType<PlopperTileEntity> teTypePlopper;
	@ObjectHolder(MOD_ID + ":plopper")
	public static ContainerType<PlopperContainer> cTypePlopper;

	public ThePlopper()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.CONFIG_SPEC);
		MinecraftForge.EVENT_BUS.addListener(this::onItemExpire);
		MinecraftForge.EVENT_BUS.addListener(this::onBlockBreak);
		//		MinecraftForge.EVENT_BUS.addListener(this::onItemToss);
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().register(new PlopperBlock());
	}

	@SubscribeEvent
	public static void registerTileEntityTypes(RegistryEvent.Register<TileEntityType<?>> event)
	{
		event.getRegistry().register(TileEntityType.Builder.create(PlopperTileEntity::new, thePlopper).build(null).setRegistryName(thePlopper.getRegistryName()));
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(new Item(new Item.Properties().group(ItemGroup.REDSTONE).maxStackSize(7)).setRegistryName(new ResourceLocation(MOD_ID, "range_upgrade")));
		event.getRegistry().register(new BlockItem(thePlopper, new Item.Properties().group(ItemGroup.REDSTONE)).setRegistryName(thePlopper.getRegistryName()));
	}

	@SubscribeEvent
	public static void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> event)
	{
		event.getRegistry().register(new ContainerType<PlopperContainer>((windowId, playerInv) -> {
			return new PlopperContainer(windowId, playerInv, new Inventory(PlopperInventory.SLOTS));
		}).setRegistryName(thePlopper.getRegistryName()));
	}

	public void onItemExpire(ItemExpireEvent event)
	{
		checkForPloppers(event.getEntityItem());
	}

	/**
	 * Checks for any ploppers in increasing ranges and makes them suck up the item if applicable
	 * @param ei The item to potentially suck up
	 */
	private static void checkForPloppers(ItemEntity ei)
	{
		if(ei.getEntityWorld().isRemote)
			return;

		for(PlopperTileEntity plopper : PlopperTracker.getPloppersInRange(ei.getEntityWorld(), ei.getPosition()))
		{
			//if there are multiple ploppers that could potentially pick up the item, this one will take as much as it can and let the rest be handled by others
			if(plopper.suckUp(ei, ei.getItem()))
				return;
		}
	}

	public void onBlockBreak(BlockEvent.BreakEvent event)
	{
		TileEntity te = event.getWorld().getTileEntity(event.getPos());

		if(te instanceof PlopperTileEntity && event.getWorld() instanceof World)
		{
			for(ItemStack stack : ((PlopperTileEntity)te).getInventory().getContents())
			{
				Block.spawnAsEntity((World)event.getWorld(), event.getPos(), stack);
			}
		}
	}

	/**
	 * For testing purposes
	 */
	//TODO: Comment out on release
	//	public void onItemToss(ItemTossEvent event)
	//	{
	//		checkForPloppers(event.getEntityItem());
	//	}
}
