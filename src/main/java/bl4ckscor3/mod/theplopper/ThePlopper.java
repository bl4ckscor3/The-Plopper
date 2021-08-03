package bl4ckscor3.mod.theplopper;

import bl4ckscor3.mod.theplopper.block.PlopperBlock;
import bl4ckscor3.mod.theplopper.block.PlopperContainer;
import bl4ckscor3.mod.theplopper.block.PlopperTileEntity;
import bl4ckscor3.mod.theplopper.tracking.PlopperTracker;
import net.minecraft.block.Block;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
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
		event.getRegistry().register(TileEntityType.Builder.of(PlopperTileEntity::new, thePlopper).build(null).setRegistryName(thePlopper.getRegistryName()));
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(new Item(new Item.Properties().tab(ItemGroup.TAB_REDSTONE).stacksTo(7)).setRegistryName(new ResourceLocation(MOD_ID, "range_upgrade")));
		event.getRegistry().register(new BlockItem(thePlopper, new Item.Properties().tab(ItemGroup.TAB_REDSTONE)).setRegistryName(thePlopper.getRegistryName()));
	}

	@SubscribeEvent
	public static void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> event)
	{
		event.getRegistry().register(IForgeContainerType.create((windowId, playerInv, data) -> new PlopperContainer(windowId, playerInv, playerInv.player.level.getBlockEntity(data.readBlockPos()))).setRegistryName(thePlopper.getRegistryName()));
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
		if(ei.getCommandSenderWorld().isClientSide)
			return;

		for(PlopperTileEntity plopper : PlopperTracker.getPloppersInRange(ei.getCommandSenderWorld(), ei.blockPosition()))
		{
			//if there are multiple ploppers that could potentially pick up the item, this one will take as much as it can and let the rest be handled by others
			if(plopper.suckUp(ei, ei.getItem()))
				return;
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
