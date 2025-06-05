package tictim.paraglider.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.network.CustomPayloadPacket;

public sealed interface Msg extends CustomPacketPayload permits BargainDialogMsg, BargainEndMsg, BargainInitMsg, BargainMsg, SyncCatalogMsg,
		SyncLookAtMsg, SyncMovementMsg, SyncPlayerStateMapMsg, SyncRemoteMovementMsg, SyncVesselMsg, SyncWindMsg{
	void write(@NotNull FriendlyByteBuf buffer);
}
