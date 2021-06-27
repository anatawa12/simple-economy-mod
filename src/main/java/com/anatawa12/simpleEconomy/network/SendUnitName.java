package com.anatawa12.simpleEconomy.network;

import com.anatawa12.simpleEconomy.SimpleEconomy;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class SendUnitName implements IMessage {
    private String unit;

    @Deprecated
    public SendUnitName() {
    }

    public SendUnitName(String unit) {
        this.unit = unit;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        byte[] bytes = new byte[buf.readInt()];
        buf.readBytes(bytes);
        unit = new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] bytes = unit.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    public static final IMessageHandler<SendUnitName, IMessage> HANDLER = (msg, ctx) -> {
        SimpleEconomy.setRemoteUnit(msg.unit);
        return null;
    };
}
