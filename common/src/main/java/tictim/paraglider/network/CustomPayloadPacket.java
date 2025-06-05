package tictim.paraglider.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import tictim.paraglider.network.message.Msg;

public class CustomPayloadPacket implements Packet<ClientGamePacketListener> {
    private final Msg msg;

    public CustomPayloadPacket(Msg msg) {
        this.msg = msg;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        msg.write(buffer);
    }

    @Override
    public void handle(ClientGamePacketListener listener) {
        // 这里你需要实现消息的处理逻辑，或者交给消息自己处理
        // 也可以存储消息类型id，反序列化等
    }
}
