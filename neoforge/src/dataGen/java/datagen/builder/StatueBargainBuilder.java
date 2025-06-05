package datagen.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.crafting.CraftingHelper;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.bargain.preview.QuantifiedIngredient;
import tictim.paraglider.bargain.preview.QuantifiedItem;
import tictim.paraglider.contents.Contents;
import tictim.paraglider.forge.contents.ForgeContents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class StatueBargainBuilder {
	protected final ResourceLocation bargainType;

	protected final List<QuantifiedIngredient> itemDemands = new ArrayList<>();
	protected int heartContainerDemands;
	protected int staminaVesselDemands;
	protected int essenceDemands;

	protected final List<QuantifiedItem> itemOffers = new ArrayList<>();
	protected int heartContainerOffers;
	protected int staminaVesselOffers;
	protected int essenceOffers;

	protected final List<ICondition> conditions = new ArrayList<>();

	public StatueBargainBuilder(ResourceLocation bargainType) {
		this.bargainType = Objects.requireNonNull(bargainType);
	}

	public StatueBargainBuilder demand(ItemLike item, int quantity) {
		return demand(Ingredient.of(item), quantity);
	}

	public StatueBargainBuilder demand(TagKey<Item> tag, int quantity) {
		return demand(Ingredient.of(tag), quantity);
	}

	public StatueBargainBuilder demand(Ingredient ingredient, int quantity) {
		itemDemands.add(new QuantifiedIngredient(ingredient, quantity));
		return this;
	}

	public StatueBargainBuilder demandHeartContainer(int quantity) {
		heartContainerDemands = quantity;
		return this;
	}

	public StatueBargainBuilder demandStaminaVessel(int quantity) {
		staminaVesselDemands = quantity;
		return this;
	}

	public StatueBargainBuilder demandEssence(int quantity) {
		essenceDemands = quantity;
		return this;
	}

	public StatueBargainBuilder offer(Item item, int count) {
		itemOffers.add(new QuantifiedItem(item, count));
		return this;
	}

	public StatueBargainBuilder offerHeartContainer(int quantity) {
		heartContainerOffers = quantity;
		return this;
	}

	public StatueBargainBuilder offerStaminaVessel(int quantity) {
		staminaVesselOffers = quantity;
		return this;
	}

	public StatueBargainBuilder offerEssence(int quantity) {
		essenceOffers = quantity;
		return this;
	}

	public StatueBargainBuilder condition(ICondition condition) {
		this.conditions.add(Objects.requireNonNull(condition));
		return this;
	}

	public void build(RecipeOutput output, ResourceLocation id) {
		output.accept(id, new BargainRecipe(id, bargainType, itemDemands, heartContainerDemands, staminaVesselDemands, essenceDemands, itemOffers, heartContainerOffers, staminaVesselOffers, essenceOffers, conditions), null);
	}

	public static class BargainRecipe implements Recipe<RecipeWrapper> {
		protected final ResourceLocation id;
		protected final ResourceLocation bargainType;

		protected final List<QuantifiedIngredient> itemDemands;
		protected final int heartContainerDemands;
		protected final int staminaVesselDemands;
		protected final int essenceDemands;

		protected final List<QuantifiedItem> itemOffers;
		protected final int heartContainerOffers;
		protected final int staminaVesselOffers;
		protected final int essenceOffers;
		protected final List<ICondition> conditions;

		public BargainRecipe (@NotNull ResourceLocation id,
							  @NotNull ResourceLocation bargainType,
							  @NotNull List<QuantifiedIngredient> itemDemands,
							  int heartContainerDemands,
							  int staminaVesselDemands,
							  int essenceDemands,
							  @NotNull List<QuantifiedItem> itemOffers,
							  int heartContainerOffers,
							  int staminaVesselOffers,
							  int essenceOffers, List<ICondition> conditions) {
			this.id = id;
			this.bargainType = bargainType;
			this.itemDemands = itemDemands;
			this.heartContainerDemands = heartContainerDemands;
			this.staminaVesselDemands = staminaVesselDemands;
			this.essenceDemands = essenceDemands;
			this.itemOffers = itemOffers;
			this.heartContainerOffers = heartContainerOffers;
			this.staminaVesselOffers = staminaVesselOffers;
			this.essenceOffers = essenceOffers;
			this.conditions = conditions;
		}

		@Override
		public boolean matches (@NotNull RecipeWrapper craftingInput, @NotNull Level level) {
			return false; // Not a real crafting recipe
		}

		@Override
		public @NotNull ItemStack assemble (@NotNull RecipeWrapper craftingInput, @NotNull RegistryAccess registryAccess) {
			return ItemStack.EMPTY; // Not a real crafting recipe
		}

		@Override
		public boolean canCraftInDimensions (int width, int height) {
			return false; // Not a real crafting recipe
		}

		@Override
		public @NotNull ItemStack getResultItem (@NotNull RegistryAccess registryAccess) {
			return ItemStack.EMPTY; // Bargains don't have traditional result items
		}

		@Override
		public @NotNull RecipeSerializer<?> getSerializer () {
			return Contents.get().bargainRecipeSerializer();
		}

		@Override
		public RecipeType<?> getType () {
			return Contents.get().bargainRecipeType();
		}

		public static final Codec<BargainRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ResourceLocation.CODEC.fieldOf("id").forGetter(r -> r.id),
				ResourceLocation.CODEC.fieldOf("bargainType").forGetter(r -> r.bargainType),

				Codec.list(QuantifiedIngredient.CODEC).fieldOf("itemDemands").forGetter(r -> r.itemDemands),
				Codec.INT.fieldOf("heartContainerDemands").forGetter(r -> r.heartContainerDemands),
				Codec.INT.fieldOf("staminaVesselDemands").forGetter(r -> r.staminaVesselDemands),
				Codec.INT.fieldOf("essenceDemands").forGetter(r -> r.essenceDemands),

				Codec.list(QuantifiedItem.CODEC).fieldOf("itemOffers").forGetter(r -> r.itemOffers),
				Codec.INT.fieldOf("heartContainerOffers").forGetter(r -> r.heartContainerOffers),
				Codec.INT.fieldOf("staminaVesselOffers").forGetter(r -> r.staminaVesselOffers),
				Codec.INT.fieldOf("essenceOffers").forGetter(r -> r.essenceOffers),

				ICondition.LIST_CODEC.optionalFieldOf("conditions", Collections.emptyList()).forGetter(r -> r.conditions)
		).apply(instance, BargainRecipe::new));
	}
}