package tictim.paraglider.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.api.ParagliderAPI;
import tictim.paraglider.wind.WindChunk;

public record SyncWindMsg(@NotNull WindChunk windChunk) implements Msg, CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(ParagliderAPI.MODID, "sync_wind_msg");

	public SyncWindMsg(FriendlyByteBuf buffer) {
		this(new WindChunk(buffer));
	}

	@NotNull public static SyncWindMsg read(@NotNull FriendlyByteBuf buffer){
		return new SyncWindMsg(buffer);
	}

	@Override public void write(@NotNull FriendlyByteBuf buf){
		windChunk.write(buf);
	}

	@Override
	public ResourceLocation id () {
		return ID;
	}
}
