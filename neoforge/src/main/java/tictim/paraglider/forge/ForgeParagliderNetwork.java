package tictim.paraglider.forge;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.api.ParagliderAPI;
import tictim.paraglider.network.ClientPacketHandler;
import tictim.paraglider.network.ParagliderNetwork;
import tictim.paraglider.network.ParagliderNetworkBase;
import tictim.paraglider.network.ServerPacketHandler;
import tictim.paraglider.network.message.BargainDialogMsg;
import tictim.paraglider.network.message.BargainEndMsg;
import tictim.paraglider.network.message.BargainInitMsg;
import tictim.paraglider.network.message.BargainMsg;
import tictim.paraglider.network.message.Msg;
import tictim.paraglider.network.message.SyncCatalogMsg;
import tictim.paraglider.network.message.SyncLookAtMsg;
import tictim.paraglider.network.message.SyncMovementMsg;
import tictim.paraglider.network.message.SyncPlayerStateMapMsg;
import tictim.paraglider.network.message.SyncRemoteMovementMsg;
import tictim.paraglider.network.message.SyncVesselMsg;
import tictim.paraglider.network.message.SyncWindMsg;

import java.util.Optional;

public final class ForgeParagliderNetwork extends ParagliderNetworkBase{
	private static final ForgeParagliderNetwork instance = new ForgeParagliderNetwork();

	public static final String NETVERSION = "2.0";

	@NotNull public static ParagliderNetwork get(){
		return instance;
	}

	// just for loading the class
	public static void init(){}

	public static void registerPayloads(RegisterPayloadHandlerEvent event) {
		IPayloadRegistrar registrar = event.registrar(ParagliderAPI.id("master").toString())
				.versioned(NETVERSION);

		// Client-bound packets
		registrar.play(SyncPlayerStateMapMsg.ID, SyncPlayerStateMapMsg::new,
				handler -> handler.client((msg, context) -> {
					context.workHandler().submitAsync(() -> ClientPacketHandler.handleSyncPlayerStateMap(msg));
				}));

		registrar.play(SyncMovementMsg.ID, SyncMovementMsg::new,
				handler -> handler.client((msg, context) -> {
					context.workHandler().submitAsync(() -> ClientPacketHandler.handleSyncMovement(msg));
				}));

		registrar.play(SyncRemoteMovementMsg.ID, SyncRemoteMovementMsg::new,
				handler -> handler.client((msg, context) -> {
					context.workHandler().submitAsync(() -> ClientPacketHandler.handleSyncRemoteMovement(msg));
				}));

		registrar.play(SyncVesselMsg.ID, SyncVesselMsg::new,
				handler -> handler.client((msg, context) -> {
					context.workHandler().submitAsync(() -> ClientPacketHandler.handleSyncVessel(msg));
				}));

		registrar.play(BargainInitMsg.ID, BargainInitMsg::new,
				handler -> handler.client((msg, context) -> {
					context.workHandler().submitAsync(() -> ClientPacketHandler.handleBargainInit(msg));
				}));

		registrar.play(SyncCatalogMsg.ID, SyncCatalogMsg::new,
				handler -> handler.client((msg, context) -> {
					context.workHandler().submitAsync(() -> ClientPacketHandler.handleSyncCatalog(msg));
				}));

		registrar.play(SyncLookAtMsg.ID, SyncLookAtMsg::new,
				handler -> handler.client((msg, context) -> {
					context.workHandler().submitAsync(() -> ClientPacketHandler.handleSyncLookAt(msg));
				}));

		registrar.play(BargainDialogMsg.ID, BargainDialogMsg::new,
				handler -> handler.client((msg, context) -> {
					context.workHandler().submitAsync(() -> ClientPacketHandler.handleBargainDialog(msg));
				}));

		registrar.play(SyncWindMsg.ID, SyncWindMsg::new,
				handler -> handler.client((msg, context) -> {
					context.workHandler().submitAsync(() -> ClientPacketHandler.handleSyncWind(msg));
				}));

		// Server-bound packets
		registrar.play(BargainMsg.ID, BargainMsg::new,
				handler -> handler.server((msg, context) -> {
					context.workHandler().submitAsync(() -> {
						Optional<Player> playerOpt = context.player();
						if (playerOpt.isPresent() && playerOpt.get() instanceof ServerPlayer serverPlayer) {
							ServerPacketHandler.handleBargain(serverPlayer, msg);
						}
					});
				}));

		// Bidirectional packets
		registrar.play(BargainEndMsg.ID, BargainEndMsg::new,
				handler -> handler.server((msg, context) -> {
					context.workHandler().submitAsync(() -> {
						Optional<Player> playerOpt = context.player();
						if (playerOpt.isPresent() && playerOpt.get() instanceof ServerPlayer serverPlayer) {
							ServerPacketHandler.handleBargainEnd(serverPlayer, msg);
						}
					});
				}));
	}

	@Override protected void sendToAll(@NotNull MinecraftServer server, @NotNull Msg msg){
		PacketDistributor.ALL.noArg().send(msg);
	}

	@Override protected void sendToPlayer(@NotNull ServerPlayer player, @NotNull Msg msg){
		PacketDistributor.PLAYER.with(player).send(msg);
	}

	@Override protected void sendToTracking(@NotNull MinecraftServer server, @NotNull Entity entity, @NotNull Msg msg){
		PacketDistributor.TRACKING_ENTITY.with(entity).send(msg);
	}

	@Override protected void sendToTracking(@NotNull MinecraftServer server, @NotNull LevelChunk chunk, @NotNull Msg msg){
		PacketDistributor.TRACKING_CHUNK.with(chunk).send(msg);
	}

	@Override protected void sendToServer(@NotNull Msg msg){
		PacketDistributor.SERVER.noArg().send(msg);
	}
}