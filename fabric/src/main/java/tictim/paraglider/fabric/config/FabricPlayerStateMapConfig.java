package tictim.paraglider.fabric.config;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.config.ModConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.paraglider.config.PlayerStateMapConfig;
import tictim.paraglider.impl.movement.PlayerStateMap;

import static tictim.paraglider.api.ParagliderAPI.MODID;

public class FabricPlayerStateMapConfig extends PlayerStateMapConfig{
	@Nullable private MinecraftServer server;

	public FabricPlayerStateMapConfig(@NotNull PlayerStateMap originalStateMap){
		super(originalStateMap);
		NeoForgeConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.COMMON, this.spec, FILENAME);
		NeoForgeModConfigEvents.reloading(MODID).register(cfg -> {
			if(cfg.getSpec()==this.spec) scheduleReload(server, null);
		});
	}

	public void setServer(@Nullable MinecraftServer server){
		this.server = server;
	}
}
