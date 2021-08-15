package com.anatawa12.simpleEconomy.gui;

import com.anatawa12.simpleEconomy.CashBoxTileEntity;
import com.anatawa12.simpleEconomy.SimpleEconomy;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
    public static final int cashBoxGui = 0;
    public static final int cashBoxAddAllowGui = 1;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te;
        switch (ID) {
            case cashBoxGui:
            case cashBoxAddAllowGui:
                SimpleEconomy.requestSyncByUuid(player.getUniqueID());
                te = world.getTileEntity(x, y, z);
                if (te instanceof CashBoxTileEntity)
                    return ((CashBoxTileEntity) te).getContainer();
                return null;
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case cashBoxGui:
                return new CashBoxGui(world, x, y, z);
            case cashBoxAddAllowGui:
                return new CashBoxAddAllowGui(world, x, y, z);
            default:
                return null;
        }
    }
}
