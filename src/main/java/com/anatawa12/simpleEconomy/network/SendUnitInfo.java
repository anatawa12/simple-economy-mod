package com.anatawa12.simpleEconomy.network;

import com.anatawa12.simpleEconomy.SimpleEconomy;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class SendUnitInfo implements IMessage {
    private SimpleEconomy.Unit unit;

    @Deprecated
    public SendUnitInfo() {
    }

    public SendUnitInfo(SimpleEconomy.Unit unit) {
        this.unit = unit;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        unit = new SimpleEconomy.Unit();
        unit.unitStr = ByteBufUtils.readUTF8String(buf);
        unit.isDecimal = buf.readBoolean();
        unit.isBefore = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf,unit.unitStr);
        buf.writeBoolean(unit.isDecimal);
        buf.writeBoolean(unit.isBefore);
    }

    public static final IMessageHandler<SendUnitInfo, IMessage> HANDLER = (msg, ctx) -> {
        SimpleEconomy.setRemoteUnit(msg.unit);
        return null;
    };
}
