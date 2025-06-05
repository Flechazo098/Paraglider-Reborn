package tictim.paraglider.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.api.ParagliderAPI;

public record BargainEndMsg(int sessionId) implements Msg, CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(ParagliderAPI.MODID, "bargain_end_msg");

	public BargainEndMsg(FriendlyByteBuf buffer) {
		this(buffer.readVarInt());
	}

	@NotNull public static BargainEndMsg read(@NotNull FriendlyByteBuf buffer){
		return new BargainEndMsg(buffer);
	}

	@Override public void write(@NotNull FriendlyByteBuf buffer){
		buffer.writeVarInt(sessionId);
	}

	@Override
	public ResourceLocation id () {
		return ID;
	}
}
