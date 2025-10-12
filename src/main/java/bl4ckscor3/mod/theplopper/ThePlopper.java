package bl4ckscor3.mod.theplopper;

import bl4ckscor3.mod.theplopper.block.PlopperBlock;
import bl4ckscor3.mod.theplopper.block.PlopperBlockEntity;
import bl4ckscor3.mod.theplopper.block.PlopperItem;
import bl4ckscor3.mod.theplopper.block.PlopperMenu;
import bl4ckscor3.mod.theplopper.tracking.PlopperTracker;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(ThePlopper.MODID)
@EventBusSubscriber
public class ThePlopper {
	public static final String MODID = "theplopper";
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, MODID);
	public static final DeferredBlock<PlopperBlock> THE_PLOPPER = BLOCKS.registerBlock("plopper", PlopperBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F, 8.0F).sound(SoundType.METAL).isRedstoneConductor((state, world, pos) -> false).requiresCorrectToolForDrops());
	public static final DeferredItem<PlopperItem> THE_PLOPPER_ITEM = ITEMS.registerItem("plopper", p -> new PlopperItem(THE_PLOPPER.get(), p.useBlockDescriptionPrefix()));
	public static final DeferredItem<Item> RANGE_UPGRADE = ITEMS.registerSimpleItem("range_upgrade", new Item.Properties().stacksTo(7));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PlopperBlockEntity>> PLOPPER_BLOCK_ENTITY_TYPE = BLOCK_ENTITY_TYPES.register("plopper", () -> new BlockEntityType<>(PlopperBlockEntity::new, THE_PLOPPER.get()));
	public static final DeferredHolder<MenuType<?>, MenuType<PlopperMenu>> PLOPPER_MENU_TYPE = MENU_TYPES.register("plopper", () -> IMenuTypeExtension.create((windowId, playerInv, data) -> new PlopperMenu(windowId, playerInv, playerInv.player.level().getBlockEntity(data.readBlockPos()))));

	public ThePlopper(IEventBus modEventBus, ModContainer modContainer) {
		BLOCKS.register(modEventBus);
		ITEMS.register(modEventBus);
		BLOCK_ENTITY_TYPES.register(modEventBus);
		MENU_TYPES.register(modEventBus);
		modContainer.registerConfig(ModConfig.Type.COMMON, Configuration.CONFIG_SPEC);
		NeoForge.EVENT_BUS.addListener(this::onItemExpire);

		if (!FMLEnvironment.isProduction()) //for testing purposes
			NeoForge.EVENT_BUS.addListener((ItemTossEvent event) -> checkForPloppers(event.getEntity()));
	}

	@SubscribeEvent
	public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.Item.BLOCK, PLOPPER_BLOCK_ENTITY_TYPE.get(), PlopperBlockEntity::getCapability);
	}

	@SubscribeEvent
	public static void onCreativeModeTabBuildContents(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS || event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
			event.accept(THE_PLOPPER_ITEM.get());
			event.accept(RANGE_UPGRADE.get());
		}
	}

	public void onItemExpire(ItemExpireEvent event) {
		checkForPloppers(event.getEntity());
	}

	/**
	 * Checks for any ploppers in increasing ranges and makes them suck up the item if applicable
	 *
	 * @param ei The item to potentially suck up
	 */
	private static void checkForPloppers(ItemEntity ei) {
		if (ei.level().isClientSide())
			return;

		for (PlopperBlockEntity plopper : PlopperTracker.getPloppersInRange(ei.level(), ei.blockPosition())) {
			//if there are multiple ploppers that could potentially pick up the item, this one will take as much as it can and let the rest be handled by others
			if (plopper.suckUp(ei))
				return;
		}
	}
}
