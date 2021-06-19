package com.anatawa12.simpleEconomy.network;

import com.anatawa12.simpleEconomy.SimpleEconomy;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import io.netty.buffer.ByteBuf;

public class SendPlayersMoney implements IMessage {
    private long money;

    @SuppressWarnings("unused")
    public SendPlayersMoney() {
    }

    public SendPlayersMoney(long money) {
        this.money = money;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        money = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(money);
    }

    public static IMessageHandler<SendPlayersMoney, IMessage> HANDLER = (msg, ctx) -> {
        SimpleEconomy.clientUsersMoney = msg.money;
        return null;
    };
}
