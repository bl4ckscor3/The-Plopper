package bl4ckscor3.mod.theplopper.datagen;

import java.util.concurrent.CompletableFuture;

import bl4ckscor3.mod.theplopper.ThePlopper;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public class RecipeGenerator extends RecipeProvider {
	private final HolderGetter<Item> items;

	public RecipeGenerator(HolderLookup.Provider lookupProvider, RecipeOutput output) {
		super(lookupProvider, output);
		items = lookupProvider.lookupOrThrow(Registries.ITEM);
	}

	@Override
	protected final void buildRecipes() {
		//@formatter:off
		ShapelessRecipeBuilder.shapeless(items,RecipeCategory.MISC, ThePlopper.THE_PLOPPER)
		.requires(Items.HOPPER)
		.requires(Items.REPEATER)
		.requires(Items.ENDER_EYE)
		.unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
		.save(output);
		ShapelessRecipeBuilder.shapeless(items,RecipeCategory.MISC, ThePlopper.RANGE_UPGRADE)
		.requires(Tags.Items.DUSTS_REDSTONE)
		.requires(Items.REPEATER)
		.requires(Items.ENDER_EYE)
		.requires(Items.PAPER)
		.unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
		.save(output);
		//@formatter:on
	}

	public static final class Runner extends RecipeProvider.Runner {
		public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(output, lookupProvider);
		}

		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider lookupProvider, RecipeOutput output) {
			return new RecipeGenerator(lookupProvider, output);
		}

		@Override
		public String getName() {
			return "Sculk Transporting recipes";
		}
	}
}
