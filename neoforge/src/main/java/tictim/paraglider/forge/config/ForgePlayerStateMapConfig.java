package tictim.paraglider.forge.config;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.config.PlayerStateMapConfig;
import tictim.paraglider.impl.movement.PlayerStateMap;

public class ForgePlayerStateMapConfig extends PlayerStateMapConfig{
	public ForgePlayerStateMapConfig(@NotNull PlayerStateMap originalStateMap, IEventBus bus) {
		super(originalStateMap);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, this.spec, FILENAME);
		bus.addListener((ModConfigEvent.Reloading event) -> {
			if(event.getConfig().getSpec()==this.spec) scheduleReload(ServerLifecycleHooks.getCurrentServer(), null);
		});
	}
}
