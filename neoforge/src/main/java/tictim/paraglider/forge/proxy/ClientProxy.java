package tictim.paraglider.forge.proxy;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import tictim.paraglider.ParagliderMod;
import tictim.paraglider.api.ParagliderAPI;
import tictim.paraglider.client.ParagliderClientSettings;
import tictim.paraglider.client.ParagliderItemColor;
import tictim.paraglider.client.ParagliderItemProperty;
import tictim.paraglider.contents.Contents;
import tictim.paraglider.forge.client.StaminaWheelOverlay;
import tictim.paraglider.impl.movement.PlayerStateMap;

public class ClientProxy extends CommonProxy{
	private final ParagliderClientSettings clientSettings = new ParagliderClientSettings(FMLPaths.GAMEDIR.get());

	@Nullable private PlayerStateMap syncedStateMap;

	@Nullable private KeyMapping paragliderSettingsKey;

	public ClientProxy (IEventBus modEventBus) {
        super(modEventBus);
        modEventBus.addListener((FMLClientSetupEvent e) -> e.enqueueWork(() -> {
			ItemProperties.register(Contents.get().paraglider(), ParagliderItemProperty.KEY_PARAGLIDING, ParagliderItemProperty.get());
			ItemProperties.register(Contents.get().dekuLeaf(), ParagliderItemProperty.KEY_PARAGLIDING, ParagliderItemProperty.get());
		}));
		modEventBus.addListener((RegisterKeyMappingsEvent e) -> e.register(paragliderSettingsKey = new KeyMapping(
				"key.paraglider.paragliderSettings",
				KeyConflictContext.IN_GAME,
				KeyModifier.CONTROL,
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_P, "key.categories.misc")));
		modEventBus.addListener((RegisterColorHandlersEvent.Item e) -> {
			e.register(new ParagliderItemColor(Contents.get().paraglider()), Contents.get().paraglider());
			e.register(new ParagliderItemColor(Contents.get().dekuLeaf()), Contents.get().dekuLeaf());
		});
		modEventBus.addListener((RegisterGuiOverlaysEvent e) -> e.registerAboveAll(new ResourceLocation(ParagliderAPI.MODID, "stamina_wheel"), new StaminaWheelOverlay()));

		clientSettings.load();
	}


	@Override protected void onServerAboutToStart(ServerAboutToStartEvent event){
		super.onServerAboutToStart(event);
		this.syncedStateMap = null;
	}

	@Override @NotNull public ParagliderClientSettings getClientSettings(){
		return clientSettings;
	}

	@Override @NotNull public PlayerStateMap getStateMap(){
		return syncedStateMap!=null ? syncedStateMap : super.getStateMap();
	}

	@Override public void setSyncedStateMap(@Nullable PlayerStateMap stateMap){
		this.syncedStateMap = stateMap;
	}

	@Override @NotNull public KeyMapping getParagliderSettingsKey(){
		if(paragliderSettingsKey==null) throw new IllegalStateException("paragliderSettingsKey is not available yet");
		return paragliderSettingsKey;
	}
}
