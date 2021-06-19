package com.anatawa12.simpleEconomy.network;

import com.anatawa12.simpleEconomy.gui.CashBoxGui;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class NoMuchMoneyError implements IMessage {
    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static final IMessageHandler<NoMuchMoneyError, IMessage> HANDLER = (msg, ctx) -> {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen instanceof CashBoxGui) {
            ((CashBoxGui)screen).onNoMuchMoneyError();
        }
        return null;
    };
}
