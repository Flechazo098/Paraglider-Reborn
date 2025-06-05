package tictim.paraglider.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.api.ParagliderAPI;
import tictim.paraglider.impl.movement.PlayerStateMap;

public record SyncPlayerStateMapMsg(@NotNull PlayerStateMap stateMap) implements Msg, CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(ParagliderAPI.MODID, "sync_player_state_map_msg");

	public SyncPlayerStateMapMsg(FriendlyByteBuf buffer) {
		this(PlayerStateMap.read(buffer));
	}

	@NotNull public static SyncPlayerStateMapMsg read(@NotNull FriendlyByteBuf buffer){
		return new SyncPlayerStateMapMsg(buffer);
	}

	@Override public void write(@NotNull FriendlyByteBuf buffer){
		stateMap.write(buffer);
	}

	@Override
	public ResourceLocation id () {
		return ID;
	}
}
