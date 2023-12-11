package bl4ckscor3.mod.theplopper.block;

import bl4ckscor3.mod.theplopper.Configuration;
import bl4ckscor3.mod.theplopper.ThePlopper;
import bl4ckscor3.mod.theplopper.tracking.PlopperTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class PlopperBlockEntity extends BlockEntity implements MenuProvider {
	public static final int SLOTS = 7;
	private NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(7, ItemStack.EMPTY);
	private NonNullList<ItemStack> upgrade = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);
	private IItemHandler inventoryHandler;
	private IItemHandler upgradeHandler;
	private boolean tracked = false;

	public PlopperBlockEntity(BlockPos pos, BlockState state) {
		super(ThePlopper.PLOPPER_BLOCK_ENTITY_TYPE.get(), pos, state);
	}

	/**
	 * Adds the given {@link net.minecraft.item.ItemStack} to the inventory
	 *
	 * @param ie The ItemEntity that gets sucked up
	 * @param stack The stack to add
	 * @return true if (part of) the stack has been sucked up, false if the stack couldn't be sucked up
	 */
	public boolean suckUp(ItemEntity ie, ItemStack stack) {
		ItemStack remainder = stack;
		IItemHandler itemHandler = getInventoryHandler();

		if (itemHandler == null)
			return false;

		for (int i = 0; i < inventory.size(); i++) {
			remainder = itemHandler.insertItem(i, remainder, false);

			if (remainder.isEmpty())
				break;
		}

		if (remainder.equals(stack)) {
			level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
			return false;
		}

		if (!remainder.isEmpty()) {
			ItemEntity newIe = new ItemEntity(ie.getCommandSenderWorld(), ie.getX(), ie.getY(), ie.getZ(), remainder);

			ie.discard();
			newIe.setDeltaMovement(0.0D, 0.0D, 0.0D);
			newIe.getCommandSenderWorld().addFreshEntity(newIe);
		}
		else
			ie.discard();

		if (!level.isClientSide && Configuration.CONFIG.displayParticles.get()) {
			((ServerLevel) level).sendParticles(ParticleTypes.SMOKE, ie.getX(), ie.getY() + 0.25D, ie.getZ(), 10, 0.0D, 0.1D, 0.0D, 0.001D);
			((ServerLevel) level).sendParticles(ParticleTypes.ENCHANT, getBlockPos().getX() + 0.5D, getBlockPos().getY() + 1.5D, getBlockPos().getZ() + 0.5D, 20, 0.0D, 0.0D, 0.0D, 0.3D);
		}

		if (Configuration.CONFIG.playSound.get())
			ie.getCommandSenderWorld().playSound(null, ie.blockPosition(), SoundEvents.CHICKEN_EGG, SoundSource.NEUTRAL, 1.0F, 1.0F);

		level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 2);
		setChanged();
		return true;
	}

	public static void tick(Level level, BlockPos pos, BlockState state, PlopperBlockEntity be) {
		if (!be.tracked) {
			PlopperTracker.track(be);
			be.tracked = true;
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();

		PlopperTracker.stopTracking(this);
		tracked = false;
	}

	@Override
	public CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void load(CompoundTag tag) {
		CompoundTag invTag = tag.getCompound("PlopperInventory");

		for (int i = 0; i < inventory.size(); i++) {
			if (invTag != null && invTag.contains("Slot" + i))
				inventory.set(i, ItemStack.of((CompoundTag) invTag.get("Slot" + i)));
		}

		upgrade.set(0, ItemStack.of(invTag.getCompound("Slot7")));
		super.load(tag);
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		CompoundTag invTag = new CompoundTag();

		for (int i = 0; i < inventory.size(); i++) {
			invTag.put("Slot" + i, inventory.get(i).save(new CompoundTag()));
		}

		invTag.put("Slot7", upgrade.get(0).save(new CompoundTag()));
		tag.put("PlopperInventory", invTag);
		super.saveAdditional(tag);
	}

	public static IItemHandler getCapability(PlopperBlockEntity be, Direction side) {
		if (side == Direction.DOWN || Configuration.CONFIG.bypassOutputSide.get())
			return new ExtractOnlyItemStackHandler(be.inventory);

		return null;
	}

	/**
	 * @return The range this plopper will pick up items in
	 */
	public AABB getRange() {
		int range = 2 + upgrade.get(0).getCount() * 2;
		int x = getBlockPos().getX();
		int y = getBlockPos().getY();
		int z = getBlockPos().getZ();
		return new AABB(x - range, y - range, z - range, x + range, y + range, z + range);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
		return new PlopperMenu(windowId, playerInv, this);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(ThePlopper.THE_PLOPPER.get().getDescriptionId());
	}

	public NonNullList<ItemStack> getInventory() {
		return inventory;
	}

	public NonNullList<ItemStack> getUpgrade() {
		return upgrade;
	}

	public IItemHandler getInventoryHandler() {
		if (inventoryHandler == null)
			inventoryHandler = new ItemStackHandler(inventory);

		return inventoryHandler;
	}

	public IItemHandler getUpgradeHandler() {
		if (upgradeHandler == null) {
			upgradeHandler = new ItemStackHandler(upgrade) {
				@Override
				public int getSlotLimit(int slot) {
					return 7;
				}
			};
		}

		return upgradeHandler;
	}
}
