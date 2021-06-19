package com.anatawa12.simpleEconomy.network;

import com.anatawa12.simpleEconomy.CashBoxTileEntity;
import com.anatawa12.simpleEconomy.gui.CashBoxContainer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

import java.util.UUID;

public class RemoveAllowedPlayer implements IMessage {
    private UUID uuid;

    @Deprecated
    public RemoveAllowedPlayer() {
    }

    public RemoveAllowedPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        uuid = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public static final IMessageHandler<RemoveAllowedPlayer, IMessage> HANDLER = (msg, ctx) -> {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        Container container = player.openContainer;
        if (container instanceof CashBoxContainer) {
            CashBoxTileEntity te = ((CashBoxContainer) container).te;
            if (te.checkAllowedOrOp(player)) {
                te.removeAllowed(msg.uuid, player);
            }
        }
        return null;
    };
}
