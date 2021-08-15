package com.anatawa12.simpleEconomy.network;

import com.anatawa12.simpleEconomy.SimpleEconomy;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import io.netty.buffer.ByteBuf;

import java.math.BigDecimal;

public class SendPlayersMoney implements IMessage {
    private String money;

    @SuppressWarnings("unused")
    public SendPlayersMoney() {
    }

    public SendPlayersMoney(String money) {
        this.money = money;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        money = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, money);
    }

    public static IMessageHandler<SendPlayersMoney, IMessage> HANDLER = (msg, ctx) -> {
        SimpleEconomy.clientUsersMoney = new BigDecimal(msg.money);
        return null;
    };
}
