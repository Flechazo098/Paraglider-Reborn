package tictim.paraglider.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.api.ParagliderAPI;

import java.util.UUID;

public record SyncRemoteMovementMsg(@NotNull UUID entityId, @NotNull ResourceLocation state) implements Msg, CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(ParagliderAPI.MODID, "sync_remote_movement_msg");

	public SyncRemoteMovementMsg(FriendlyByteBuf buffer) {
		this(buffer.readUUID(), buffer.readResourceLocation());
	}

	@NotNull public static SyncRemoteMovementMsg read(@NotNull FriendlyByteBuf buffer){
		return new SyncRemoteMovementMsg(buffer);
	}

	@Override public void write(@NotNull FriendlyByteBuf buffer){
		buffer.writeUUID(entityId);
		buffer.writeResourceLocation(state);
	}

	@Override
	public ResourceLocation id () {
		return ID;
	}
}
