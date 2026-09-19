package bl4ckscor3.mod.theplopper.datagen;

import bl4ckscor3.mod.theplopper.ThePlopper;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeGenerator extends RecipeProvider {
	public static final TagKey<Item> DUSTS_REDSTONE = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "dusts/redstone"));
	private final HolderGetter<Item> items;

	public RecipeGenerator(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
		super(recipeOutput, advancementOutput);
		items = recipeOutput.lookup(Registries.ITEM);
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
}
