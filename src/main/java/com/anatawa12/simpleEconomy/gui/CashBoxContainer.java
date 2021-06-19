package com.anatawa12.simpleEconomy.gui;

import com.anatawa12.simpleEconomy.CashBoxTileEntity;
import com.anatawa12.simpleEconomy.network.NetworkHandler;
import com.anatawa12.simpleEconomy.network.SendCashBoxInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CashBoxContainer extends Container {
    public final CashBoxTileEntity te;
    private final List<EntityPlayerMP> players = new ArrayList<>();

    public CashBoxContainer(CashBoxTileEntity te) {
        this.te = te;
    }

    @SideOnly(Side.CLIENT)
    public CashBoxContainer() {
        this.te = null;
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_) {
        return true;
    }

    @Override
    public void addCraftingToCrafters(ICrafting p_75132_1_) {
        if (p_75132_1_ instanceof EntityPlayerMP) {
            assert te != null;
            List<Pair<UUID, String>> allowed = te.getAllowedPlayersInfo();
            NetworkHandler.sendToClient(new SendCashBoxInfo(te.getMoney(), allowed), (EntityPlayerMP) p_75132_1_);
            players.add((EntityPlayerMP) p_75132_1_);
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer p_75134_1_) {
        super.onContainerClosed(p_75134_1_);
        if (p_75134_1_ instanceof EntityPlayerMP) {
            players.remove((EntityPlayerMP) p_75134_1_);
        }
    }

    public void sendUpdate() {
        assert te != null;
        List<Pair<UUID, String>> allowed = te.getAllowedPlayersInfo();
        for (EntityPlayerMP player : players) {
            NetworkHandler.sendToClient(new SendCashBoxInfo(te.getMoney(), allowed), player);
        }
    }
}
