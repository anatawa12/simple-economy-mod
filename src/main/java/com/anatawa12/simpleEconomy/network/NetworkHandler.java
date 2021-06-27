package com.anatawa12.simpleEconomy.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;

public class NetworkHandler {
    private static final SimpleNetworkWrapper NETWORK = new SimpleNetworkWrapper("simple-economy");

    public static void init() {
        NETWORK.registerMessage(MoveCacheWithBox.HANDLER, MoveCacheWithBox.class, 0, Side.SERVER);
        NETWORK.registerMessage(RemoveAllowedPlayer.HANDLER, RemoveAllowedPlayer.class, 1, Side.SERVER);
        NETWORK.registerMessage(AddAllowedPlayer.HANDLER, AddAllowedPlayer.class, 2, Side.SERVER);
        NETWORK.registerMessage(SendPlayersMoney.HANDLER, SendPlayersMoney.class, 3, Side.CLIENT);
        NETWORK.registerMessage(SendCashBoxInfo.HANDLER, SendCashBoxInfo.class, 4, Side.CLIENT);
        NETWORK.registerMessage(NoMuchMoneyError.HANDLER, NoMuchMoneyError.class, 5, Side.CLIENT);
        NETWORK.registerMessage(SendPlayersInfo.HANDLER, SendPlayersInfo.class, 6, Side.CLIENT);
        NETWORK.registerMessage(RequestStartAddingAllowedPlayer.HANDLER, RequestStartAddingAllowedPlayer.class, 7, Side.SERVER);
        NETWORK.registerMessage(SendUnitName.HANDLER, SendUnitName.class, 8, Side.CLIENT);
    }

    public static void sendToClient(IMessage message, EntityPlayerMP to) {
        NETWORK.sendTo(message, to);
    }

    public static void sendToServer(IMessage message) {
        NETWORK.sendToServer(message);
    }
}
