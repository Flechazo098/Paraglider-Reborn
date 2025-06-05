package tictim.paraglider.forge.config;

import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import tictim.paraglider.config.LocalConfig;

public final class ForgeConfig extends LocalConfig{

	public ForgeConfig(IEventBus modEventBus) {
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, this.spec);
		modEventBus.addListener(this::onLoad);
		modEventBus.addListener(this::onReload);
	}


	private void onLoad(ModConfigEvent.Loading event){
		if(event.getConfig().getSpec()==this.spec) reloadWindSources();
	}

	private void onReload(ModConfigEvent.Reloading event){
		if(event.getConfig().getSpec()==this.spec){
			MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
			if(server!=null) server.execute(this::reloadWindSources);
		}
	}
}
