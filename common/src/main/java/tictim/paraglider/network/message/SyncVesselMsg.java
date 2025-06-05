package tictim.paraglider.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.api.ParagliderAPI;

public record SyncVesselMsg(int stamina, int heartContainers, int staminaVessels) implements Msg, CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(ParagliderAPI.MODID, "sync_vessel_msg");

	public SyncVesselMsg(FriendlyByteBuf buffer) {
		this(
				buffer.readVarInt(),
				buffer.readVarInt(),
				buffer.readVarInt());
	}

	@NotNull public static SyncVesselMsg read(@NotNull FriendlyByteBuf buffer){
		return new SyncVesselMsg(buffer);
	}

	@Override public void write(@NotNull FriendlyByteBuf buffer){
		buffer.writeInt(stamina);
		buffer.writeVarInt(heartContainers);
		buffer.writeVarInt(staminaVessels);
	}

	@Override
	public ResourceLocation id () {
		return ID;
	}
}
