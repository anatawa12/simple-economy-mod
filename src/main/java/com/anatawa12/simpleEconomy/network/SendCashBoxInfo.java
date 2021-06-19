package com.anatawa12.simpleEconomy.network;

import com.anatawa12.simpleEconomy.Utils;
import com.anatawa12.simpleEconomy.gui.CashBoxGui;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SendCashBoxInfo implements IMessage {
    private long money;
    private List<Pair<UUID, String>> allowed;

    @Deprecated
    public SendCashBoxInfo() {
    }

    public SendCashBoxInfo(long money, List<Pair<UUID, String>> allowed) {
        this.money = money;
        this.allowed = allowed;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        money = buf.readLong();
        int allowedLen = buf.readInt();
        allowed = new ArrayList<>(allowedLen);
        for (int i = 0; i < allowedLen; i++) {
            allowed.add(Pair.of(
                    new UUID(buf.readLong(),
                            buf.readLong()),
                    Utils.readString(buf)));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(money);
        buf.writeInt(allowed.size());
        for (Pair<UUID, String> uuidStringPair : allowed) {
            buf.writeLong(uuidStringPair.getLeft().getMostSignificantBits());
            buf.writeLong(uuidStringPair.getLeft().getLeastSignificantBits());
            Utils.writeString(buf, uuidStringPair.getRight());
        }
    }

    public static IMessageHandler<SendCashBoxInfo, IMessage> HANDLER = (msg, ctx) -> {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen instanceof CashBoxGui) {
            ((CashBoxGui)screen).setCashBoxInfo(msg.money, msg.allowed);
        }
        return null;
    };
}
