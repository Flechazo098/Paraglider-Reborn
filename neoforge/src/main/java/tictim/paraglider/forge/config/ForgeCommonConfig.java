package tictim.paraglider.forge.config;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import tictim.paraglider.config.CommonConfig;

public final class ForgeCommonConfig extends CommonConfig{
	public ForgeCommonConfig(IEventBus modEventBus) {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, this.spec);
	}
}
