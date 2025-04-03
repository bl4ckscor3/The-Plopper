package bl4ckscor3.mod.theplopper.datagen;

import java.util.concurrent.CompletableFuture;

import bl4ckscor3.mod.theplopper.ThePlopper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

public class BlockTagGenerator extends BlockTagsProvider {
	public BlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, ThePlopper.MODID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ThePlopper.THE_PLOPPER.get());
	}
}
