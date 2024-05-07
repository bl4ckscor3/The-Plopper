package bl4ckscor3.mod.theplopper.datagen;

import java.util.concurrent.CompletableFuture;

import bl4ckscor3.mod.theplopper.ThePlopper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockTagGenerator extends BlockTagsProvider {
	public BlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, ThePlopper.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ThePlopper.THE_PLOPPER.get());
	}
}
