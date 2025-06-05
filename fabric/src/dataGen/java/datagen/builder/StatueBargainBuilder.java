package datagen.builder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.paraglider.bargain.preview.QuantifiedIngredient;
import tictim.paraglider.bargain.preview.QuantifiedItem;
import tictim.paraglider.contents.Contents;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class StatueBargainBuilder{
	protected final ResourceLocation bargainType;

	protected final List<QuantifiedIngredient> itemDemands = new ArrayList<>();
	protected int heartContainerDemands;
	protected int staminaVesselDemands;
	protected int essenceDemands;

	protected final List<QuantifiedItem> itemOffers = new ArrayList<>();
	protected int heartContainerOffers;
	protected int staminaVesselOffers;
	protected int essenceOffers;

	protected boolean featureFlags;

	public StatueBargainBuilder(ResourceLocation bargainType){
		this.bargainType = Objects.requireNonNull(bargainType);
	}

	public StatueBargainBuilder demand(ItemLike item, int quantity){
		return demand(Ingredient.of(item), quantity);
	}
	public StatueBargainBuilder demand(TagKey<Item> tag, int quantity){
		return demand(Ingredient.of(tag), quantity);
	}
	public StatueBargainBuilder demand(Ingredient ingredient, int quantity){
		itemDemands.add(new QuantifiedIngredient(ingredient, quantity));
		return this;
	}
	public StatueBargainBuilder demandHeartContainer(int quantity){
		heartContainerDemands = quantity;
		return this;
	}
	public StatueBargainBuilder demandStaminaVessel(int quantity){
		staminaVesselDemands = quantity;
		return this;
	}
	public StatueBargainBuilder demandEssence(int quantity){
		essenceDemands = quantity;
		return this;
	}

	public StatueBargainBuilder offer(Item item, int count){
		itemOffers.add(new QuantifiedItem(item, count));
		return this;
	}
	public StatueBargainBuilder offerHeartContainer(int quantity){
		heartContainerOffers = quantity;
		return this;
	}
	public StatueBargainBuilder offerStaminaVessel(int quantity){
		staminaVesselOffers = quantity;
		return this;
	}
	public StatueBargainBuilder offerEssence(int quantity){
		essenceOffers = quantity;
		return this;
	}

	public StatueBargainBuilder disableFeatureFlags(){
		featureFlags = false;
		return this;
	}

	public void build(RecipeOutput output, ResourceLocation id){
		output.accept(id, new BargainRecipe(id,
				bargainType,
				itemDemands,
				heartContainerDemands,
				staminaVesselDemands,
				essenceDemands,
				itemOffers,
				heartContainerOffers,
				staminaVesselOffers,
				essenceOffers,
				featureFlags),
				null);
	}

	public static class BargainRecipe implements Recipe<Container> {
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

		protected final boolean disableFlags;

		public BargainRecipe(@NotNull ResourceLocation id,
		              @NotNull ResourceLocation bargainType,
		              @NotNull List<QuantifiedIngredient> itemDemands,
		              int heartContainerDemands,
		              int staminaVesselDemands,
		              int essenceDemands,
		              @NotNull List<QuantifiedItem> itemOffers,
		              int heartContainerOffers,
		              int staminaVesselOffers,
		              int essenceOffers,
		              boolean disableFlags){
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
			this.disableFlags = disableFlags;
		}

		public static final Codec<BargainRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ResourceLocation.CODEC.fieldOf("id").forGetter(r -> r.id),
				ResourceLocation.CODEC.fieldOf("bargainType").forGetter(r -> r.bargainType),
				Demands.CODEC.fieldOf("demands").forGetter(r -> new Demands(r.itemDemands, r.heartContainerDemands, r.staminaVesselDemands, r.essenceDemands)),
				Offers.CODEC.fieldOf("offers").forGetter(r -> new Offers(r.itemOffers, r.heartContainerOffers, r.staminaVesselOffers, r.essenceOffers)),
				Codec.BOOL.optionalFieldOf("disableFlags", false).forGetter(r -> r.disableFlags)
		).apply(instance, (id, type, demands, offers, flags) -> new BargainRecipe(
				id, type,
				demands.items(), demands.heart(), demands.stamina(), demands.essence(),
				offers.items(), offers.heart(), offers.stamina(), offers.essence(),
				flags
		)));


		public record Demands(
				List<QuantifiedIngredient> items,
				int heart,
				int stamina,
				int essence
		) {
			public static final Codec<Demands> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					Codec.list(QuantifiedIngredient.CODEC).optionalFieldOf("items", List.of()).forGetter(Demands::items),
					Codec.INT.optionalFieldOf("heartContainers", 0).forGetter(Demands::heart),
					Codec.INT.optionalFieldOf("staminaVessels", 0).forGetter(Demands::stamina),
					Codec.INT.optionalFieldOf("essences", 0).forGetter(Demands::essence)
			).apply(instance, Demands::new));
		}

		public record Offers(
				List<QuantifiedItem> items,
				int heart,
				int stamina,
				int essence
		) {
			public static final Codec<Offers> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					Codec.list(QuantifiedItem.CODEC).optionalFieldOf("items", List.of()).forGetter(Offers::items),
					Codec.INT.optionalFieldOf("heartContainers", 0).forGetter(Offers::heart),
					Codec.INT.optionalFieldOf("staminaVessels", 0).forGetter(Offers::stamina),
					Codec.INT.optionalFieldOf("essences", 0).forGetter(Offers::essence)
			).apply(instance, Offers::new));
		}

		@Override
		public boolean matches (@NotNull Container craftingInput, @NotNull Level level) {
			return false; // Not a real crafting recipe
		}

		@Override
		public @NotNull ItemStack assemble (@NotNull Container craftingInput, @NotNull RegistryAccess registryAccess) {
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
	}
}
