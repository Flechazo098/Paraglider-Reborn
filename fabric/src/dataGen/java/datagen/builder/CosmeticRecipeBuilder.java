package datagen.builder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.paraglider.contents.Contents;

import java.util.Objects;
import java.util.function.Consumer;

public class CosmeticRecipeBuilder {
	private final Item result;
	private final Ingredient input;
	private final Ingredient reagent;
	private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
	private String group;
	private RecipeCategory recipeCategory = RecipeCategory.MISC;

	public CosmeticRecipeBuilder(Item result, Ingredient input, Ingredient reagent) {
		this.result = result;
		this.input = input;
		this.reagent = reagent;
	}

	public CosmeticRecipeBuilder addCriterion(String name, CriterionTriggerInstance criterionIn) {
		this.advancementBuilder.addCriterion(name, impossible());
		return this;
	}


	public static Criterion<ImpossibleTrigger.TriggerInstance> impossible() {
		return new Criterion<>(new ImpossibleTrigger(), new ImpossibleTrigger.TriggerInstance());
	}

	public CosmeticRecipeBuilder setGroup(String group) {
		this.group = group;
		return this;
	}

	public CosmeticRecipeBuilder recipeCategory(RecipeCategory category) {
		this.recipeCategory = Objects.requireNonNull(category);
		return this;
	}

	public void build(RecipeOutput output) {
		this.build(output, Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(this.result)));
	}

	public void build(RecipeOutput output, String save) {
		ResourceLocation resourcelocation = BuiltInRegistries.ITEM.getKey(this.result);
		if ((new ResourceLocation(save)).equals(resourcelocation))
			throw new IllegalStateException("Paraglider Cosmetic Recipe " + save + " should remove its 'save' argument");
		this.build(output, new ResourceLocation(save));
	}

	public void build(RecipeOutput output, ResourceLocation id) {
		this.validate(id);
		ResourceLocation advancementId = id.withPrefix("recipes/" + recipeCategory.getFolderName() + "/");
		Advancement.Builder builder = output.advancement();
		builder.parent(new ResourceLocation("recipes/root"));
		builder.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id));
		builder.rewards(AdvancementRewards.Builder.recipe(id));
		builder.requirements(AdvancementRequirements.Strategy.OR);
		AdvancementHolder advancementHolder = builder.build(advancementId);

		output.accept(id, new CosmeticRecipe(this.result, this.group == null ? "" : this.group, this.input, this.reagent), advancementHolder);
	}

	private void validate(ResourceLocation id) {
		try {
			this.advancementBuilder.build(id);
		} catch (IllegalStateException e) {
			throw new IllegalStateException("No way of obtaining recipe " + id);
		}
	}

	public static class CosmeticRecipe implements Recipe<Container> {
		private final Item result;
		private final String group;
		private final Ingredient input;
		private final Ingredient reagent;

		public CosmeticRecipe(Item result, String group, Ingredient input, Ingredient reagent) {
			this.result = result;
			this.group = group;
			this.input = input;
			this.reagent = reagent;
		}

		@Override
		public boolean matches(@NotNull Container craftingInput, @NotNull Level level) {
			return false; // Not a real crafting recipe, just for data generation
		}

		@Override
		public @NotNull ItemStack assemble(@NotNull Container craftingInput, @NotNull net.minecraft.core.RegistryAccess registryAccess) {
			return ItemStack.EMPTY; // Not a real crafting recipe
		}

		@Override
		public boolean canCraftInDimensions(int width, int height) {
			return false; // Not a real crafting recipe
		}

		@Override
		public @NotNull ItemStack getResultItem(@NotNull net.minecraft.core.RegistryAccess registryAccess) {
			return new ItemStack(this.result);
		}

		@Override
		public @NotNull RecipeSerializer<?> getSerializer() {
			return Contents.get().cosmeticRecipeSerializer();
		}

		@Override
		public RecipeType<?> getType () {
			return Contents.get().bargainRecipeType();
		}

		public static final Codec<CosmeticRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ItemStack.CODEC.fieldOf("result").forGetter(r -> new ItemStack(r.result)),
				Codec.STRING.optionalFieldOf("group", "").forGetter(r -> r.group),
				Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(r -> r.input),
				Ingredient.CODEC_NONEMPTY.fieldOf("reagent").forGetter(r -> r.reagent)
		).apply(instance, (resultStack, group, input, reagent) -> new CosmeticRecipe(resultStack.getItem(), group, input, reagent)));
	}
}