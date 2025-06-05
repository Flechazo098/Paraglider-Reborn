package tictim.paraglider.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tictim.paraglider.api.ParagliderAPI;
import tictim.paraglider.network.NetUtils;

public record SyncLookAtMsg(int sessionId, @Nullable Vec3 lookAt) implements Msg, CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(ParagliderAPI.MODID, "sync_look_at_msg");

	public SyncLookAtMsg(FriendlyByteBuf buffer) {
		this(buffer.readVarInt(), NetUtils.readLookAt(buffer));
	}

	@NotNull public static SyncLookAtMsg read(@NotNull FriendlyByteBuf buffer){
		return new SyncLookAtMsg(buffer);
	}

	@Override public void write(@NotNull FriendlyByteBuf buffer){
		buffer.writeVarInt(sessionId);
		NetUtils.writeLookAt(buffer, lookAt);
	}

	@Override
	public ResourceLocation id () {
		return ID;
	}
}
