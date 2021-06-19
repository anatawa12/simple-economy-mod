package com.anatawa12.simpleEconomy.network;

import com.anatawa12.simpleEconomy.CashBoxTileEntity;
import com.anatawa12.simpleEconomy.gui.CashBoxContainer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class MoveCacheWithBox implements IMessage {
    private int mount;

    @Deprecated
    public MoveCacheWithBox() {
    }

    // positive: user -> box
    // negative: box -> user
    public MoveCacheWithBox(int mount) {
        this.mount = mount;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        mount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(mount);
    }

    public static IMessageHandler<MoveCacheWithBox, IMessage> HANDLER = (msg, ctx) -> {
        EntityPlayerMP playerMP = ctx.getServerHandler().playerEntity;
        if (!(playerMP.openContainer instanceof CashBoxContainer)) return null;
        CashBoxTileEntity tileEntity = ((CashBoxContainer) playerMP.openContainer).te;
        if (tileEntity == null) return null;
        if (!tileEntity.checkAllowedOrOp(playerMP)) return null;
        if (!tileEntity.moveMoney(playerMP, msg.mount)) {
            return new NoMuchMoneyError();
        }
        return null;
    };
}
