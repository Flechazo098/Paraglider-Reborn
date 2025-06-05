package tictim.paraglider.forge.proxy;

import com.mojang.serialization.Codec;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.paraglider.ParagliderUtils;
import tictim.paraglider.api.ParagliderAPI;
import tictim.paraglider.client.ParagliderClientSettings;
import tictim.paraglider.command.ParagliderCommands;
import tictim.paraglider.config.PlayerStateMapConfig;
import tictim.paraglider.contents.Contents;
import tictim.paraglider.contents.ParagliderVillageStructures;
import tictim.paraglider.forge.ForgeParagliderNetwork;
import tictim.paraglider.forge.attachment.PlayerMovementAttachment;
import tictim.paraglider.forge.config.ForgePlayerStateMapConfig;
import tictim.paraglider.forge.contents.ConfigConditionSerializer;
import tictim.paraglider.impl.movement.*;
import tictim.paraglider.impl.stamina.NullStamina;
import tictim.paraglider.impl.stamina.StaminaFactoryLoader;
import tictim.paraglider.impl.vessel.NullVesselContainer;
import tictim.paraglider.network.ParagliderNetwork;


public class CommonProxy{
	private final PlayerStateMapConfig stateMapConfig;
	private final PlayerStateConnectionMap connectionMap;

	private static final DeferredRegister<Codec<? extends ICondition>> CONDITION_CODECS =
			DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, ParagliderAPI.MODID);


	static {
		CONDITION_CODECS.register("heart_container_enabled",
                ConfigConditionSerializer.HEART_CONTAINER_ENABLED_CODEC::codec);

		CONDITION_CODECS.register("stamina_vessel_enabled",
                ConfigConditionSerializer.STAMINA_VESSEL_ENABLED_CODEC::codec);

	}

	public CommonProxy(IEventBus modEventBus) {

		modEventBus.addListener((FMLCommonSetupEvent e) -> e.enqueueWork(() -> {
			CauldronInteraction.WATER.map().put(Contents.get().paraglider(), CauldronInteraction.DYED_ITEM);
			CauldronInteraction.WATER.map().put(Contents.get().dekuLeaf(), CauldronInteraction.DYED_ITEM);
		}));

		PlayerMovementAttachment.register(modEventBus);

		ParagliderAPI.setMovementSupplier(p -> {
			PlayerMovement m = PlayerMovementAttachment.has(p) ? PlayerMovementAttachment.get(p) : null;
			return m==null ? NullMovement.get() : m;
		});
		ParagliderAPI.setStaminaSupplier(p -> {
			PlayerMovement m = PlayerMovementAttachment.has(p) ? PlayerMovementAttachment.get(p) : null;
			return m==null ? NullStamina.get() : m.stamina();
		});
		ParagliderAPI.setVesselContainerSupplier(p -> {
			PlayerMovement m = PlayerMovementAttachment.has(p) ? PlayerMovementAttachment.get(p) : null;
			return m==null ? NullVesselContainer.get() : m.vessels();
		});

		var pair = PlayerStateMapLoader.loadStates();
		this.stateMapConfig = new ForgePlayerStateMapConfig(pair.getFirst(), modEventBus);
		this.connectionMap = pair.getSecond();
		ParagliderAPI.setStaminaFactory(StaminaFactoryLoader.loadStaminaFactory());
		StaminaReductionLogicHandler.init();

		NeoForge.EVENT_BUS.addListener(this::onServerAboutToStart);
		NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> e.getDispatcher().register(ParagliderCommands.register()));
		// OnDatapackSyncEvent without player is called on datapack reload
		NeoForge.EVENT_BUS.addListener((OnDatapackSyncEvent e) -> {
			MinecraftServer server = e.getPlayerList().getServer();
			ParagliderUtils.checkBargainRecipes(server);
			ParagliderVillageStructures.addVillageStructures(server);
		});
		NeoForge.EVENT_BUS.addListener((ServerStoppingEvent e) -> this.stateMapConfig.removeCallbacks());

		ForgeParagliderNetwork.init();
		CONDITION_CODECS.register(modEventBus);
	}

	protected void onServerAboutToStart(ServerAboutToStartEvent event){
		MinecraftServer server = event.getServer();
		ParagliderVillageStructures.addVillageStructures(server);
		PlayerStateMapConfig stateMapConfig = this.stateMapConfig;
		stateMapConfig.removeCallbacks();
		stateMapConfig.reload();
		ParagliderUtils.printPlayerStates(stateMapConfig.stateMap(), getConnectionMap());
		stateMapConfig.addCallback(stateMap -> {
			ParagliderUtils.printPlayerStates(stateMap, getConnectionMap());
			ParagliderNetwork.get().syncStateMapToAll(server, stateMap);
		});
		ParagliderUtils.checkBargainRecipes(server);
	}

	@NotNull public ParagliderClientSettings getClientSettings(){
		throw new IllegalStateException("Trying to access client settings in server environment");
	}

	@NotNull public PlayerStateMap getStateMap(){
		return getLocalStateMap();
	}
	@NotNull public PlayerStateMap getLocalStateMap(){
		return stateMapConfig.stateMap();
	}
	@NotNull public PlayerStateConnectionMap getConnectionMap(){
		return connectionMap;
	}
	@NotNull public PlayerStateMapConfig getStateMapConfig(){
		return stateMapConfig;
	}

	public void setSyncedStateMap(@Nullable PlayerStateMap stateMap){
		throw new IllegalStateException("Trying to access client side value in server environment");
	}

	@OnlyIn(Dist.CLIENT)
	@NotNull public KeyMapping getParagliderSettingsKey(){
		throw new AssertionError();
	}
}
