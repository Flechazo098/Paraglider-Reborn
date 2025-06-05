package tictim.paraglider.forge.contents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;
import tictim.paraglider.api.ParagliderAPI;
import tictim.paraglider.config.FeatureCfg;

import java.util.Locale;

public enum ConfigConditionSerializer implements ICondition {
	HEART_CONTAINER_ENABLED,
	STAMINA_VESSEL_ENABLED;

	private final ResourceLocation id = ParagliderAPI.id(name().toLowerCase(Locale.ROOT));

	public static final MapCodec<ConfigConditionSerializer> HEART_CONTAINER_ENABLED_CODEC =
			MapCodec.unit(HEART_CONTAINER_ENABLED);

	public static final MapCodec<ConfigConditionSerializer> STAMINA_VESSEL_ENABLED_CODEC =
			MapCodec.unit(STAMINA_VESSEL_ENABLED);

	@Override public boolean test(IContext context){
		return switch(this){
			case HEART_CONTAINER_ENABLED -> FeatureCfg.get().enableHeartContainers();
			case STAMINA_VESSEL_ENABLED -> FeatureCfg.get().enableStaminaVessels();
		};
	}

	@Override
	public Codec<? extends ICondition> codec() {
		return (switch(this) {
					case HEART_CONTAINER_ENABLED -> HEART_CONTAINER_ENABLED_CODEC;
					case STAMINA_VESSEL_ENABLED -> STAMINA_VESSEL_ENABLED_CODEC;
				}).stable().codec();
	}
}