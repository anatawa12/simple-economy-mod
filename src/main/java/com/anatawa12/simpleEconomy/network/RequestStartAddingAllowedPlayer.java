package com.anatawa12.simpleEconomy.network;

import com.anatawa12.simpleEconomy.MoneyManager;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import io.netty.buffer.ByteBuf;

public class RequestStartAddingAllowedPlayer implements IMessage {
    public RequestStartAddingAllowedPlayer() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static IMessageHandler<RequestStartAddingAllowedPlayer, IMessage> HANDLER = 
            (msg, ctx) -> new SendPlayersInfo(MoneyManager.getPlayersInfo());
}
