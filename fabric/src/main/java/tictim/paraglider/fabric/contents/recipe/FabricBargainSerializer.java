package tictim.paraglider.fabric.contents.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.bargain.preview.QuantifiedIngredient;
import tictim.paraglider.bargain.preview.QuantifiedItem;
import tictim.paraglider.contents.recipe.SimpleBargainSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FabricBargainSerializer extends SimpleBargainSerializer<FabricBargain> {

	public static final Codec<FabricBargain> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("id").forGetter(FabricBargain::getId),
			ResourceLocation.CODEC.fieldOf("bargainType").forGetter(FabricBargain::getBargainType),
			Codec.list(QuantifiedIngredient.CODEC).fieldOf("itemDemands").forGetter(FabricBargain::getItemDemands),
			Codec.INT.fieldOf("heartContainerDemands").forGetter(FabricBargain::getHeartContainerDemands),
			Codec.INT.fieldOf("staminaVesselDemands").forGetter(FabricBargain::getStaminaVesselDemands),
			Codec.INT.fieldOf("essenceDemands").forGetter(FabricBargain::getEssenceDemands),
			Codec.list(QuantifiedItem.CODEC).fieldOf("itemOffers").forGetter(FabricBargain::getItemOffers),
			Codec.INT.fieldOf("heartContainerOffers").forGetter(FabricBargain::getHeartContainerOffers),
			Codec.INT.fieldOf("staminaVesselOffers").forGetter(FabricBargain::getStaminaVesselOffers),
			Codec.INT.fieldOf("essenceOffers").forGetter(FabricBargain::getEssenceOffers),
			Codec.list(Codec.STRING).fieldOf("userTags")
					.forGetter(sb -> new ArrayList<>(sb.getUserTags())),
			Codec.BOOL.fieldOf("usesHeartContainerFeature").forGetter(FabricBargain::usesHeartContainerFeature),
			Codec.BOOL.fieldOf("usesStaminaVesselFeature").forGetter(FabricBargain::usesStaminaVesselFeature)
	).apply(instance, (id, bargainType, itemDemands, heartDemands, staminaDemands, essenceDemands,
					   itemOffers, heartOffers, staminaOffers, essenceOffers, userTagsList, usesHeartContainerFeature, usesStaminaVesselFeature) ->
			new FabricBargain(
					id,
					bargainType,
					itemDemands,
					heartDemands,
					staminaDemands,
					essenceDemands,
					itemOffers,
					heartOffers,
					staminaOffers,
					essenceOffers,
					Set.copyOf(userTagsList),
					usesHeartContainerFeature,
					usesStaminaVesselFeature
			)
	));

	@Override
	@NotNull
	protected FabricBargain instantiate (@NotNull ResourceLocation recipeId,
										 @NotNull JsonObject json,
										 @NotNull ResourceLocation bargainType,
										 @NotNull List<@NotNull QuantifiedIngredient> itemDemands,
										 int heartContainerDemands,
										 int staminaVesselDemands,
										 int essenceDemands,
										 @NotNull List<@NotNull QuantifiedItem> itemOffers,
										 int heartContainerOffers,
										 int staminaVesselOffers,
										 int essenceOffers,
										 @NotNull Set<@NotNull String> userTags) {
		return new FabricBargain(recipeId, bargainType, itemDemands, heartContainerDemands, staminaVesselDemands,
				essenceDemands, itemOffers, heartContainerOffers, staminaVesselOffers, essenceOffers, userTags,
				GsonHelper.getAsBoolean(json, "usesHeartContainerFeature", false),
				GsonHelper.getAsBoolean(json, "usesStaminaVesselFeature", false));
	}

	// Recipe constructed from packets don't need any special treatment, since the two boolean flags are only used on server side
	@Override
	@NotNull
	protected FabricBargain instantiate (@NotNull ResourceLocation recipeId,
										 @NotNull FriendlyByteBuf buffer,
										 @NotNull ResourceLocation bargainType,
										 @NotNull List<@NotNull QuantifiedIngredient> itemDemands,
										 int heartContainerDemands,
										 int staminaVesselDemands,
										 int essenceDemands,
										 @NotNull List<@NotNull QuantifiedItem> itemOffers,
										 int heartContainerOffers,
										 int staminaVesselOffers,
										 int essenceOffers,
										 @NotNull Set<@NotNull String> userTags) {
		return new FabricBargain(recipeId, bargainType, itemDemands, heartContainerDemands, staminaVesselDemands,
				essenceDemands, itemOffers, heartContainerOffers, staminaVesselOffers, essenceOffers, userTags,
				false, false);
	}

	@Override
	public Codec<FabricBargain> codec () {
		return CODEC;
	}
}
