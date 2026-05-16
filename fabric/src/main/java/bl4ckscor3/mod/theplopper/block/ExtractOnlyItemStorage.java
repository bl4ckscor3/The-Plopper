package bl4ckscor3.mod.theplopper.block;

import java.util.Iterator;

import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ExtractionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class ExtractOnlyItemStorage implements ExtractionOnlyStorage<ItemVariant> {
	private final ContainerStorage containerStorage;

	public ExtractOnlyItemStorage(ContainerStorage containerStorage) {
		this.containerStorage = containerStorage;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return containerStorage.extract(resource, maxAmount, transaction);
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator() {
		return containerStorage.iterator();
	}
}
