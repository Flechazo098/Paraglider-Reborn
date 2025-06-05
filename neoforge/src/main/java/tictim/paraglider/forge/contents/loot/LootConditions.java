package tictim.paraglider.forge.contents.loot;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.config.Cfg;
import tictim.paraglider.forge.ForgeParagliderMod;
import tictim.paraglider.forge.contents.ForgeContents;

public enum LootConditions implements LootItemCondition, LootItemCondition.Builder {
	WITHER_DROPS_VESSEL,
	SPIRIT_ORB_LOOTS;

	public static final Codec<LootConditions> CODEC_WITHER_DROPS_VESSEL = Codec.unit(WITHER_DROPS_VESSEL);
	public static final Codec<LootConditions> CODEC_SPIRIT_ORB_LOOTS = Codec.unit(SPIRIT_ORB_LOOTS);

	@Override
	public LootItemConditionType getType() {
		ForgeContents contents = ForgeParagliderMod.instance().getContents();
		return switch(this) {
			case WITHER_DROPS_VESSEL -> contents.WITHER_DROPS_VESSEL_CONFIG_CONDITION.get();
			case SPIRIT_ORB_LOOTS -> contents.SPIRIT_ORB_LOOTS_CONFIG_CONDITION.get();
		};
	}

	@Override
	public boolean test(@NotNull LootContext lootContext) {
		return switch(this) {
			case WITHER_DROPS_VESSEL -> Cfg.get().witherDropsVessel();
			case SPIRIT_ORB_LOOTS -> Cfg.get().spiritOrbLoots();
		};
	}

	@Override
	public LootItemCondition build() {
		return this;
	}
}
