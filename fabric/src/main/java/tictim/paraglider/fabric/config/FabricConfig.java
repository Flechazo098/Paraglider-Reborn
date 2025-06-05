package tictim.paraglider.fabric.config;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.neoforged.fml.config.ModConfig;
import tictim.paraglider.config.LocalConfig;

import static tictim.paraglider.api.ParagliderAPI.MODID;

public class FabricConfig extends LocalConfig{
	public FabricConfig(){
		NeoForgeConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.SERVER, this.spec);
		NeoForgeModConfigEvents.loading(MODID).register(this::onLoad);
		NeoForgeModConfigEvents.reloading(MODID).register(this::onReload);
	}

	private void onLoad(ModConfig cfg){
		if(cfg.getSpec()==this.spec) reloadWindSources();
	}

	private void onReload(ModConfig cfg){
		if(cfg.getSpec()==this.spec){
			reloadWindSources();
		}
	}
}
