package bl4ckscor3.mod.theplopper.datagen;

import java.util.List;
import java.util.Set;

import bl4ckscor3.mod.theplopper.ThePlopper;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

@EventBusSubscriber(modid = ThePlopper.MODID)
public class DataGenRegistrar {
	private DataGenRegistrar() {}

	@SubscribeEvent
	public static void onGatherData(GatherDataEvent.Client event) {
		event.createProvider(BlockTagGenerator::new);
	}

	@SubscribeEvent
	public static void onGatherDataRegistryEntries(GatherDataRegistryEntriesEvent event) {
		event.add(RecipeProvider.asBootstrap(RecipeGenerator::new));
		event.add(Registries.LOOT_TABLE, new LootTableProvider(Set.of(), List.of(new SubProviderEntry(provider -> new BlockLootTableGenerator(provider) {
			@Override
			protected Iterable<Block> getKnownBlocks() {
				return List.of(ThePlopper.THE_PLOPPER.get());
			}
		}, LootContextParamSets.BLOCK))));
	}
}
