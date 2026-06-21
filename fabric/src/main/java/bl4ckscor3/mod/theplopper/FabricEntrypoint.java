package bl4ckscor3.mod.theplopper;

import java.util.Optional;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import bl4ckscor3.mod.theplopper.block.ExtractOnlyItemStorage;
import bl4ckscor3.mod.theplopper.block.PlopperBlockEntity;
import bl4ckscor3.mod.theplopper.lib.Platform;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.fml.config.ModConfig;

public class FabricEntrypoint implements ModInitializer, Platform {
	@Override
	public void onInitialize() {
		ThePlopper.initialize(this);
		ConfigRegistry.INSTANCE.register(ThePlopper.MODID, ModConfig.Type.COMMON, Configuration.CONFIG_SPEC);
		ItemStorage.SIDED.registerForBlockEntities((be, side) -> {
			if (side == Direction.DOWN || Configuration.CONFIG.bypassOutputSide.get())
				return new ExtractOnlyItemStorage(ContainerStorage.of((Container) be, side));

			return null;
		}, ThePlopper.PLOPPER_BLOCK_ENTITY_TYPE.get());
	}

	@Override
	public void openPlopperMenu(ServerPlayer player, MenuProvider be, BlockPos pos) {
		player.openMenu(new ExtendedMenuProvider<>() {
			@Override
			public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
				return be.createMenu(containerId, inventory, player);
			}

			@Override
			public Component getDisplayName() {
				return be.getDisplayName();
			}

			@Override
			public BlockPos getScreenOpeningData(ServerPlayer player) {
				return pos;
			}
		});
	}

	@Override
	public <T extends BlockEntity> BlockEntityType<T> createBlockEntity(BlockEntityFactory<T> factory, Block validBlock) {
		return FabricBlockEntityTypeBuilder.create(factory::create, validBlock).build();
	}

	@Override
	public <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuTypeFactory<T> factory) {
		return new ExtendedMenuType<>(factory::create, BlockPos.STREAM_CODEC);
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public <R, T extends R> void register(ResourceKey<? extends Registry<R>> registryKey, Supplier<T> entry, String path) {
		Optional<Holder.Reference<R>> registry = BuiltInRegistries.REGISTRY.get((ResourceKey) registryKey);

		if (registry.isEmpty()) {
			throw new IllegalArgumentException("Couldn't find registry " + registryKey);
		}

		Registry.register((Registry<R>) registry.get().value(), ThePlopper.id(path), entry.get());
	}

	@Override
	public boolean suckUp(PlopperBlockEntity be, ItemEntity ie, ItemStack stack, int toInsert) {
		ContainerStorage itemHandler = ContainerStorage.of(be, null);

		try (Transaction transaction = Transaction.openOuter()) {
			int inserted = (int) StorageUtil.tryInsertStacking(itemHandler, ItemVariant.of(stack), toInsert, transaction);

			if (!be.handleSucking(ie, inserted, toInsert, stack))
				return false;

			transaction.commit();
		}

		return true;
	}
}
