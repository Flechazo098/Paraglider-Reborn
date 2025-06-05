package tictim.paraglider.fabric.config;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.neoforged.fml.config.ModConfig;
import tictim.paraglider.config.CommonConfig;

import static tictim.paraglider.api.ParagliderAPI.MODID;

public class FabricCommonConfig extends CommonConfig{
	public FabricCommonConfig(){
		NeoForgeConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.COMMON, this.spec);
	}
}
