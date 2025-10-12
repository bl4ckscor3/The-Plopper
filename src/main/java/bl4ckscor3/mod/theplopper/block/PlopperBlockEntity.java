package bl4ckscor3.mod.theplopper.block;

import bl4ckscor3.mod.theplopper.Configuration;
import bl4ckscor3.mod.theplopper.ThePlopper;
import bl4ckscor3.mod.theplopper.tracking.PlopperTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class PlopperBlockEntity extends BaseContainerBlockEntity {
	public static final int STORAGE_SIZE = 7;
	public static final int UPGRADE_SLOT = STORAGE_SIZE;
	public static final int STORAGE_SIZE_WITH_UPGRADE = STORAGE_SIZE + 1;
	private NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(STORAGE_SIZE_WITH_UPGRADE, ItemStack.EMPTY);
	private boolean tracked = false;

	public PlopperBlockEntity(BlockPos pos, BlockState state) {
		super(ThePlopper.PLOPPER_BLOCK_ENTITY_TYPE.get(), pos, state);
	}

	/**
	 * Adds the given {@link net.minecraft.world.item.ItemStack} to the inventory
	 *
	 * @param ie The ItemEntity that gets sucked up
	 * @return true if (part of) the stack has been sucked up, false if the stack couldn't be sucked up
	 */
	public boolean suckUp(ItemEntity ie) {
		ItemStack stack = ie.getItem().copy();
		int toInsert = stack.getCount();
		ResourceHandler<ItemResource> itemHandler = VanillaContainerWrapper.of(this);

		try (Transaction transaction = Transaction.openRoot()) {
			int inserted = ResourceHandlerUtil.insertStacking(itemHandler, ItemResource.of(stack), toInsert, transaction);
			BlockState state = getBlockState();

			if (inserted == 0) {
				level.sendBlockUpdated(worldPosition, state, state, 2);
				return false;
			}

			if (inserted != toInsert) {
				stack.shrink(inserted);

				ItemEntity newIe = new ItemEntity(ie.level(), ie.getX(), ie.getY(), ie.getZ(), stack);

				ie.discard();
				newIe.setDeltaMovement(0.0D, 0.0D, 0.0D);
				newIe.level().addFreshEntity(newIe);
			}
			else
				ie.discard();

			if (!level.isClientSide() && Configuration.CONFIG.displayParticles.get()) {
				((ServerLevel) level).sendParticles(ParticleTypes.SMOKE, ie.getX(), ie.getY() + 0.25D, ie.getZ(), 10, 0.0D, 0.1D, 0.0D, 0.001D);
				((ServerLevel) level).sendParticles(ParticleTypes.ENCHANT, getBlockPos().getX() + 0.5D, getBlockPos().getY() + 1.5D, getBlockPos().getZ() + 0.5D, 20, 0.0D, 0.0D, 0.0D, 0.3D);
			}

			if (Configuration.CONFIG.playSound.get())
				ie.level().playSound(null, ie.blockPosition(), SoundEvents.CHICKEN_EGG, SoundSource.NEUTRAL, 1.0F, 1.0F);

			level.sendBlockUpdated(worldPosition, state, state, 2);
			setChanged();
			transaction.commit();
		}

		return true;
	}

	public static void tick(Level level, BlockPos pos, BlockState state, PlopperBlockEntity be) {
		if (!be.tracked) {
			PlopperTracker.track(be);
			be.tracked = true;
		}
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		Containers.dropContents(level, pos, getItems());
		super.preRemoveSideEffects(pos, state);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();

		PlopperTracker.stopTracking(this);
		tracked = false;
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
		return saveCustomOnly(lookupProvider);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void loadAdditional(ValueInput tag) {
		ValueInput invTag = tag.child("PlopperInventory").orElse(null);

		if (invTag != null) {
			for (int i = 0; i < inventory.size(); i++) {
				inventory.set(i, invTag.read("Slot" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY));
			}
		}

		super.loadAdditional(tag);
	}

	@Override
	public void saveAdditional(ValueOutput tag) {
		ValueOutput invTag = tag.child("PlopperInventory");

		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.get(i);

			if (!stack.isEmpty())
				invTag.store("Slot" + i, ItemStack.CODEC, stack);
		}

		super.saveAdditional(tag);
	}

	public static ResourceHandler<ItemResource> getCapability(PlopperBlockEntity be, Direction side) {
		if (side == Direction.DOWN || Configuration.CONFIG.bypassOutputSide.get())
			return new ExtractOnlyResourceHandler(be.inventory);

		return null;
	}

	/**
	 * @return The range this plopper will pick up items in
	 */
	public AABB getRange() {
		int range = 2 + getUpgrade().getCount() * 2;
		int x = getBlockPos().getX();
		int y = getBlockPos().getY();
		int z = getBlockPos().getZ();
		return new AABB(x - range, y - range, z - range, x + range, y + range, z + range);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory playerInv) {
		return new PlopperMenu(windowId, playerInv, this);
	}

	@Override
	public Component getDefaultName() {
		return Component.translatable(ThePlopper.THE_PLOPPER.get().getDescriptionId());
	}

	@Override
	protected void setItems(NonNullList<ItemStack> stacks) {
		inventory = stacks;
	}

	@Override
	public NonNullList<ItemStack> getItems() {
		return inventory;
	}

	@Override
	public int getContainerSize() {
		return STORAGE_SIZE_WITH_UPGRADE;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return slot != UPGRADE_SLOT || stack.is(ThePlopper.THE_PLOPPER_ITEM) && getItem(slot).getCount() + stack.getCount() <= 7;
	}

	public ItemStack getUpgrade() {
		return inventory.get(UPGRADE_SLOT);
	}
}
