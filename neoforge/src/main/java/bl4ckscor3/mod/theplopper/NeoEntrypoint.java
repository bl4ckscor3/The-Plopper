package bl4ckscor3.mod.theplopper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import bl4ckscor3.mod.theplopper.block.ExtractOnlyResourceHandler;
import bl4ckscor3.mod.theplopper.block.PlopperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;

@Mod(ThePlopper.MODID)
@EventBusSubscriber
public class NeoEntrypoint implements Platform {
	private final Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> registers = new HashMap<>();
	private final IEventBus modBus;

	public NeoEntrypoint(ModContainer modContainer, IEventBus modBus) {
		this.modBus = modBus;
		ThePlopper.initialize(this);
		modContainer.registerConfig(ModConfig.Type.COMMON, Configuration.CONFIG_SPEC);
		NeoForge.EVENT_BUS.addListener((ItemExpireEvent event) -> ThePlopper.checkForPloppers(event.getEntity()));
	}

	@SubscribeEvent
	public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.Item.BLOCK, ThePlopper.PLOPPER_BLOCK_ENTITY_TYPE.get(), (be, side) -> {
			if (side == Direction.DOWN || Configuration.CONFIG.bypassOutputSide.get())
				return new ExtractOnlyResourceHandler(be.getItems());

			return null;
		});
	}

	@Override
	public void openPlopperMenu(ServerPlayer player, MenuProvider be, BlockPos pos) {
		player.openMenu(be, pos);
	}

	@Override
	public <T extends BlockEntity> BlockEntityType<T> createBlockEntity(BlockEntityFactory<T> factory, Block validBlock) {
		return new BlockEntityType<>(factory::create, validBlock);
	}

	@Override
	public <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuTypeFactory<T> factory) {
		return IMenuTypeExtension.create((windowId, inv, data) -> factory.create(windowId, inv, data.readBlockPos()));
	}

	@Override
	public <R, T extends R> void register(ResourceKey<? extends Registry<R>> registry, Supplier<T> entry, String path) {
		@SuppressWarnings("unchecked")
		DeferredRegister<R> register = (DeferredRegister<R>) registers.computeIfAbsent(
			registry,
			_ -> {
				DeferredRegister<R> r = DeferredRegister.create(registry, ThePlopper.MODID);

				r.register(modBus);
				return r;
			}
		);
		register.register(path, entry);
	}

	@Override
	public boolean suckUp(PlopperBlockEntity be, ItemEntity ie, ItemStack stack, int toInsert) {
		ResourceHandler<ItemResource> itemHandler = VanillaContainerWrapper.of(be);

		try (Transaction transaction = Transaction.openRoot()) {
			int inserted = ResourceHandlerUtil.insertStacking(itemHandler, ItemResource.of(stack), toInsert, transaction);

			if (!be.handleSucking(ie, inserted, toInsert, stack))
				return false;

			transaction.commit();
		}

		return true;
	}
}
