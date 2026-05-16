package bl4ckscor3.mod.theplopper;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import bl4ckscor3.mod.theplopper.block.PlopperBlock;
import bl4ckscor3.mod.theplopper.block.PlopperBlockEntity;
import bl4ckscor3.mod.theplopper.block.PlopperItem;
import bl4ckscor3.mod.theplopper.block.PlopperMenu;
import bl4ckscor3.mod.theplopper.tracking.PlopperTracker;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ThePlopper {
	public static final String MODID = "theplopper";
	private static Platform platform;
	public static final RegistryObject<PlopperBlock> THE_PLOPPER = RegistryObject.block("plopper", PlopperBlock::new, () -> BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F, 8.0F).sound(SoundType.METAL).isRedstoneConductor((state, world, pos) -> false).requiresCorrectToolForDrops());
	public static final RegistryObject<PlopperItem> THE_PLOPPER_ITEM = RegistryObject.blockItem("plopper", p -> new PlopperItem(THE_PLOPPER.get(), p), Item.Properties::new);
	public static final RegistryObject<Item> RANGE_UPGRADE = RegistryObject.item("range_upgrade", Item::new, () -> new Item.Properties().stacksTo(7));
	public static final Supplier<BlockEntityType<PlopperBlockEntity>> PLOPPER_BLOCK_ENTITY_TYPE = Suppliers.memoize(() -> platform.createBlockEntity(PlopperBlockEntity::new, THE_PLOPPER.get()));
	public static final Supplier<MenuType<PlopperMenu>> PLOPPER_MENU_TYPE = Suppliers.memoize(() -> platform.createMenuType(PlopperMenu::new));

	public synchronized static void initialize(Platform platform) {
		if (ThePlopper.platform != null) {
			throw new IllegalArgumentException(MODID + " platform has already been initialized");
		}

		ThePlopper.platform = platform;
		platform.register(Registries.BLOCK, THE_PLOPPER);
		platform.register(Registries.ITEM, THE_PLOPPER_ITEM);
		platform.register(Registries.ITEM, RANGE_UPGRADE);
		platform.register(Registries.BLOCK_ENTITY_TYPE, PLOPPER_BLOCK_ENTITY_TYPE, "plopper");
		platform.register(Registries.MENU, PLOPPER_MENU_TYPE, "plopper");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MODID, path);
	}

	public static Platform platform() {
		return platform;
	}

	/**
	 * Checks for any ploppers in increasing ranges and makes them suck up the item if applicable
	 *
	 * @param ei The item to potentially suck up
	 */
	public static void checkForPloppers(ItemEntity ei) {
		if (ei.level().isClientSide())
			return;

		for (PlopperBlockEntity plopper : PlopperTracker.getPloppersInRange(ei.level(), ei.blockPosition())) {
			ItemStack stack = ei.getItem().copy();

			//if there are multiple ploppers that could potentially pick up the item, this one will take as much as it can and let the rest be handled by others
			if (platform.suckUp(plopper, ei, stack, stack.getCount()))
				return;
		}
	}
}
