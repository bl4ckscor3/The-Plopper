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
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class RecipeGenerator extends RecipeProvider {
	public static final TagKey<Item> DUSTS_REDSTONE = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "dusts/redstone"));
	private final HolderGetter<Item> items;

	public RecipeGenerator(HolderLookup.Provider lookupProvider, RecipeOutput output) {
		super(lookupProvider, output);
		items = lookupProvider.lookupOrThrow(Registries.ITEM);
	}

	@Override
	public final void buildRecipes() {
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ThePlopper.THE_PLOPPER.get())
			.requires(Items.HOPPER)
			.requires(Items.REPEATER)
			.requires(Items.ENDER_EYE)
			.unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
			.save(output);
		ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, ThePlopper.RANGE_UPGRADE.get())
			.requires(DUSTS_REDSTONE)
			.requires(Items.REPEATER)
			.requires(Items.ENDER_EYE)
			.requires(Items.PAPER)
			.unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
			.save(output);
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
			return "The Plopper recipes";
		}
	}
}
