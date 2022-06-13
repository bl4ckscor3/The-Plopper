package bl4ckscor3.mod.theplopper;

import bl4ckscor3.mod.theplopper.block.PlopperBlock;
import bl4ckscor3.mod.theplopper.block.PlopperBlockEntity;
import bl4ckscor3.mod.theplopper.block.PlopperMenu;
import bl4ckscor3.mod.theplopper.tracking.PlopperTracker;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(ThePlopper.MOD_ID)
@EventBusSubscriber(bus=Bus.MOD)
public class ThePlopper
{
	public static final String MOD_ID = "theplopper";
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MOD_ID);
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, MOD_ID);
	public static final RegistryObject<PlopperBlock> THE_PLOPPER = BLOCKS.register("plopper", () -> new PlopperBlock(Block.Properties.of(Material.METAL, MaterialColor.STONE).strength(3.0F, 8.0F).sound(SoundType.METAL).isRedstoneConductor((state, world, pos) -> false)));
	public static final RegistryObject<BlockItem> THE_PLOPPER_ITEM = ITEMS.register("plopper", () -> new BlockItem(THE_PLOPPER.get(), new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)));
	public static final RegistryObject<Item> RANGE_UPGRADE = ITEMS.register("range_upgrade", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE).stacksTo(7)));
	public static final RegistryObject<BlockEntityType<PlopperBlockEntity>> PLOPPER_BLOCK_ENTITY_TYPE = BLOCK_ENTITY_TYPES.register("plopper", () -> BlockEntityType.Builder.of(PlopperBlockEntity::new, THE_PLOPPER.get()).build(null));
	public static final RegistryObject<MenuType<PlopperMenu>> PLOPPER_MENU_TYPE = MENU_TYPES.register("plopper", () -> IForgeMenuType.create((windowId, playerInv, data) -> new PlopperMenu(windowId, playerInv, playerInv.player.level.getBlockEntity(data.readBlockPos()))));

	public ThePlopper()
	{
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		BLOCKS.register(modEventBus);
		ITEMS.register(modEventBus);
		BLOCK_ENTITY_TYPES.register(modEventBus);
		MENU_TYPES.register(modEventBus);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.CONFIG_SPEC);
		MinecraftForge.EVENT_BUS.addListener(this::onItemExpire);
		//MinecraftForge.EVENT_BUS.addListener(this::onItemToss);
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

		for(PlopperBlockEntity plopper : PlopperTracker.getPloppersInRange(ei.getCommandSenderWorld(), ei.blockPosition()))
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
