package tictim.paraglider.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import tictim.paraglider.api.ParagliderAPI;
import tictim.paraglider.bargain.BargainCatalog;
import tictim.paraglider.network.NetUtils;

import java.util.Map;

public record SyncCatalogMsg(int sessionId, @NotNull Map<ResourceLocation, BargainCatalog> catalog) implements Msg, CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(ParagliderAPI.MODID, "sync_catalog_msg");

	public SyncCatalogMsg(FriendlyByteBuf buffer) {
		this(buffer.readVarInt(), NetUtils.readCatalogs(buffer));
	}

	@NotNull public static SyncCatalogMsg read(@NotNull FriendlyByteBuf buffer){
		return new SyncCatalogMsg(buffer);
	}

	@Override public void write(@NotNull FriendlyByteBuf buffer){
		buffer.writeVarInt(sessionId);
		NetUtils.writeCatalogs(buffer, catalog);
	}

	@Override
	public ResourceLocation id () {
		return ID;
	}
}
