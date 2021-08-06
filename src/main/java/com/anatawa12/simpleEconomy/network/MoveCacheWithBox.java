package com.anatawa12.simpleEconomy.network;

import com.anatawa12.simpleEconomy.CashBoxTileEntity;
import com.anatawa12.simpleEconomy.gui.CashBoxContainer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.math.BigDecimal;

public class MoveCacheWithBox implements IMessage {
    private String mount;

    @Deprecated
    public MoveCacheWithBox() {
    }

    // positive: user -> box
    // negative: box -> user
    public MoveCacheWithBox(BigDecimal mount) {
        this.mount = mount.toString();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        mount = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf,mount);
    }

    public static IMessageHandler<MoveCacheWithBox, IMessage> HANDLER = (msg, ctx) -> {
        EntityPlayerMP playerMP = ctx.getServerHandler().playerEntity;
        if (!(playerMP.openContainer instanceof CashBoxContainer)) return null;
        CashBoxTileEntity tileEntity = ((CashBoxContainer) playerMP.openContainer).te;
        if (tileEntity == null) return null;
        if (!tileEntity.checkAllowedOrOp(playerMP)) return null;
        if (!tileEntity.moveMoney(playerMP, new BigDecimal(msg.mount))) {
            return new NoMuchMoneyError();
        }
        return null;
    };
}
