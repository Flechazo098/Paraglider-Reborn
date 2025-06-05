package tictim.paraglider.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.api.ParagliderAPI;

public record SyncMovementMsg(
		@NotNull ResourceLocation state,
		int stamina,
		boolean depleted,
		int recoveryDelay,
		double reductionRate
) implements Msg, CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(ParagliderAPI.MODID, "sync_movement_msg");

	public SyncMovementMsg(FriendlyByteBuf buffer) {
		this(
				buffer.readResourceLocation(),
				buffer.readInt(),
				buffer.readBoolean(),
				buffer.readVarInt(),
				buffer.readDouble()
		);
	}

	@NotNull public static SyncMovementMsg read(@NotNull FriendlyByteBuf buffer){
		return new SyncMovementMsg(buffer);
	}

	@Override public void write(@NotNull FriendlyByteBuf buffer){
		buffer.writeResourceLocation(state);
		buffer.writeInt(stamina);
		buffer.writeBoolean(depleted);
		buffer.writeVarInt(recoveryDelay);
		buffer.writeDouble(reductionRate);
	}

	@Override
	public ResourceLocation id () {
		return ID;
	}
}
