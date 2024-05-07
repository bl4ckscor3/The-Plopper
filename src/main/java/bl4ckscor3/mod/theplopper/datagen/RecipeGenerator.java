package bl4ckscor3.mod.theplopper.datagen;

import java.util.concurrent.CompletableFuture;

import bl4ckscor3.mod.theplopper.ThePlopper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

public class RecipeGenerator extends RecipeProvider {
	public RecipeGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
	}

	@Override
	protected final void buildRecipes(RecipeOutput recipeOutput) {
		//@formatter:off
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ThePlopper.THE_PLOPPER)
		.requires(Items.HOPPER)
		.requires(Items.REPEATER)
		.requires(Items.ENDER_EYE)
		.unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
		.save(recipeOutput);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ThePlopper.RANGE_UPGRADE)
		.requires(Tags.Items.DUSTS_REDSTONE)
		.requires(Items.REPEATER)
		.requires(Items.ENDER_EYE)
		.requires(Items.PAPER)
		.unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
		.save(recipeOutput);
		//@formatter:on
	}
}
