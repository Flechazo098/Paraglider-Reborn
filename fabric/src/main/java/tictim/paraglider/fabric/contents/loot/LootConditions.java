package tictim.paraglider.fabric.contents.loot;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.config.Cfg;
import tictim.paraglider.config.FeatureCfg;

public enum LootConditions implements LootItemCondition, LootItemCondition.Builder {
	WITHER_DROPS_VESSEL,
	SPIRIT_ORB_LOOTS,
	FEATURES_SPIRIT_ORBS;

	public static final Codec<? extends LootItemCondition> CODEC_WITHER_DROPS_VESSEL =
			Codec.unit(WITHER_DROPS_VESSEL);
	public static final Codec<? extends LootItemCondition> CODEC_SPIRIT_ORB_LOOTS =
			Codec.unit(SPIRIT_ORB_LOOTS);
	public static final Codec<? extends LootItemCondition> CODEC_FEATURES_SPIRIT_ORBS =
			Codec.unit(FEATURES_SPIRIT_ORBS);

	@Override
	public LootItemConditionType getType() {
		return switch (this) {
			case WITHER_DROPS_VESSEL -> ParagliderLoots.WITHER_DROPS_VESSEL;
			case SPIRIT_ORB_LOOTS -> ParagliderLoots.SPIRIT_ORB_LOOTS;
			case FEATURES_SPIRIT_ORBS -> ParagliderLoots.FEATURES_SPIRIT_ORBS;
		};
	}

	@Override
	public boolean test(@NotNull LootContext lootContext) {
		return switch (this) {
			case WITHER_DROPS_VESSEL -> Cfg.get().witherDropsVessel();
			case SPIRIT_ORB_LOOTS -> Cfg.get().spiritOrbLoots();
			case FEATURES_SPIRIT_ORBS -> FeatureCfg.get().enableSpiritOrbGens();
		};
	}

	@Override
	public LootItemCondition build() {
		return this;
	}
}
