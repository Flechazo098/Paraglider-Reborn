package tictim.paraglider.fabric.mixin;

import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.network.ParagliderNetwork;
import tictim.paraglider.wind.Wind;
import tictim.paraglider.wind.WindChunk;

@Mixin(ChunkMap.class)
public abstract class MixinChunkMap {

	@Inject(
			at = @At("RETURN"),
			method = "markChunkPendingToSend(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/chunk/LevelChunk;)V"
	)
	private static void onMarkChunkPendingToSend(ServerPlayer player, LevelChunk chunk, CallbackInfo info) {
		Level level = chunk.getLevel();
		if (!(level instanceof ServerLevel serverLevel)) return;

		Wind wind = Wind.of(serverLevel);
		if (wind == null) return;

		ChunkPos pos = chunk.getPos();
		WindChunk windChunk = wind.getChunk(pos);
		if (windChunk == null || windChunk.isEmpty()) return;

		ParagliderNetwork.get().syncWind(serverLevel.getServer(), serverLevel.getChunk(pos.x, pos.z), windChunk);
	}
}
